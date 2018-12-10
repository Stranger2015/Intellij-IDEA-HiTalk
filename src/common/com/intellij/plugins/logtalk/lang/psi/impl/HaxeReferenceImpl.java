/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2018 AS3Boyan
 * Copyright 2014-2014 Elias Ku
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.plugins.logtalk.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugins.haxe.ide.LogtalkLookupElement;
import com.intellij.plugins.haxe.ide.refactoring.move.LogtalkFileMoveHandler;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.*;
import com.intellij.plugins.haxe.util.*;
import com.intellij.plugins.logtalk.ide.LogtalkLookupElement;
import com.intellij.plugins.logtalk.ide.refactoring.move.LogtalkFileMoveHandler;
import com.intellij.plugins.logtalk.lang.psi.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.tree.JavaSourceUtil;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.openapi.util.text.StringUtil.defaultIfEmpty;

abstract public class LogtalkReferenceImpl extends LogtalkExpressionImpl implements LogtalkReference {

  public static final LogtalkDebugLogger LOG = LogtalkDebugLogger.getLogger();
  public static final String DOT = ".";

  //static {
  //  LOG.setLevel(Level.TRACE);
  //}  // TODO: Pull this out after debugging.

  public LogtalkReferenceImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiElement getElement() {
    return this;
  }

  @Override
  public PsiReference getReference() {
    return this;
  }

  @Override
  public TextRange getRangeInElement() {
    PsiElement nameElement = getReferenceNameElement();
    if (nameElement != null) {
      int startOffset = nameElement.getStartOffsetInParent();
      return new TextRange(startOffset, startOffset + nameElement.getTextLength());
    }

    PsiElement dot = getLastChild();
    if (DOT.equals(dot.getText())) {
      int index = dot.getStartOffsetInParent() + dot.getTextLength();
      return new TextRange(index, index);
    }

    return new TextRange(0, getTextLength());
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return getText();
  }

  @Override
  public int getTextOffset() {
    PsiElement nameElement = getReferenceNameElement();
    return nameElement != null ? nameElement.getTextOffset() : super.getTextOffset();
  }

  @Nullable
  public LogtalkGenericSpecialization getSpecialization() {
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg(null));
    // CallExpressions need to resolve their child, rather than themselves.
    LogtalkExpression expression = this;
    if (this instanceof LogtalkCallExpression) {
      expression = ((LogtalkCallExpression)this).getExpression();
    } else if (this instanceof LogtalkNewExpression) {
      LogtalkNewExpression newExpression = (LogtalkNewExpression)this;
      LogtalkClass haxeClass = (LogtalkClass)newExpression.getType().getReferenceExpression().resolve();
      final LogtalkClassResolveResult result = LogtalkClassResolveResult.create(haxeClass);
      result.specializeByParameters(newExpression.getType().getTypeParam());
      return result.getSpecialization();
    }

    // The specialization for a reference comes from either the type of the left-hand side of the
    // expression, or failing that, from the class in which the reference appears, which is
    // exactly what tryGetLeftResolveResult() gives us.
    final LogtalkClassResolveResult result = tryGetLeftResolveResult(expression);
    return result != LogtalkClassResolveResult.EMPTY ? result.getSpecialization() : null;
  }

  @Override
  public boolean isSoft() {
    return false;
  }

  private List<? extends PsiElement> resolveNamesToParents(List<? extends PsiElement> nameList) {
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("namelist: " + nameList.toString()));
    if (nameList == null || nameList.isEmpty()) {
      return Collections.emptyList();
    }

    List<PsiElement> result = new ArrayList<>();
    for (PsiElement element : nameList) {
      PsiElement elementToAdd = element;
      if (element instanceof LogtalkComponentName) {
        PsiElement parent = element.getParent();
        if (null != parent && parent.isValid()) {
          // Don't look for package parents. It turns 'com' into 'com.xx'.
          // XXX: May need to walk the tree until we get to the PACKAGE_STATEMENT
          // element;
          if (!(parent instanceof PsiPackage)) {
            elementToAdd = parent;
          }
        }
      }
      result.add(elementToAdd);
    }
    return result;
  }

  @Override
  public PsiElement resolve() {
    return resolve(true);
  }

  public boolean resolveIsStaticExtension() {
    // @TODO: DIRTY HACK! to avoid rewriting all the code!
    LogtalkResolver.INSTANCE.resolve(this, true);
    return LogtalkResolver.isExtension.get();
  }

  @NotNull
  @Override
  public JavaResolveResult advancedResolve(boolean incompleteCode) {
    final PsiElement resolved = resolve(incompleteCode);
    // TODO: Determine if we are using the right substitutor.
    // ?? XXX: Is the internal element here supposed to be a PsiClass sub-class ??
    return null != resolved ? new CandidateInfo(resolved, EmptySubstitutor.getInstance()) : JavaResolveResult.EMPTY;
  }

  @NotNull
  private JavaResolveResult[] multiResolve(boolean incompleteCode, boolean resolveToParents) {
    //
    // Resolving through this.resolve, or through the ResolveCache.resolve,
    // resolves to the *name* of the component.  That's what is cached, that's
    // what is returned.  For the Java processing code, the various reference types
    // are sub-classed, along with the base reference being aware of the type of the
    // entity.  Still, the base reference (PsiJavaReference) resolves to the
    // COMPONENT_NAME element, NOT the element type.  The various sub-classes
    // of PsiJavaReference (and PsiJavaCodeReferenceElement) return the actual
    // element type.  For example, you have to have to use PsiClassType.resolve
    // to get back a PsiClassType.
    //
    // For the Logtalk code, we don't have a large number of reference sub-classes,
    // so we have to figure out what the expected parent type is and return that.
    // Luckily, most references have a COMPONENT_NAME element located immediately
    // below the parent in the PSI tree.  Therefore, when requested, we're going
    // to return the parent type.
    //
    // The root of the problem appears to be that the Java language processing
    // always expected the COMPONENT_NAME field.  However, the Logtalk processing
    // (plugin) code was written to expect the type *containing* the
    // COMPONENT_NAME element (e.g. the named element not the name of the element).
    // Therefore, we now have an adapter, and have to tweak some things to make them
    // compatible.  Perhaps the proper answer is to make all of the plug-in code
    // expect the COMPONENT_NAME field, to be consistent, and then we won't need
    // the resolveToParents logic (here, at least).
    //

    // For the moment (while debugging the resolver) let's do this without caching.
    boolean skipCachingForDebug = false;

    // If we are in dumb mode (e.g. we are still indexing files and resolving may
    // fail until the indices are complete), we don't want to cache the (likely incorrect)
    // results.
    boolean skipCaching = skipCachingForDebug || DumbService.isDumb(getProject());
    List<? extends PsiElement> cachedNames
      = skipCaching ? (LogtalkResolver.INSTANCE).resolve(this, incompleteCode)
                    : ResolveCache.getInstance(getProject())
                        .resolveWithCaching(this, LogtalkResolver.INSTANCE, true, incompleteCode);


    // CandidateInfo does some extra resolution work when checking validity, so
    // the results have to be turned into a CandidateInfoArray, and not just passed
    // around as the list that LogtalkResolver returns.
    return toCandidateInfoArray(resolveToParents ? resolveNamesToParents(cachedNames) : cachedNames);
  }

  /**
   * Resolve a reference, returning the COMPONENT_NAME field of the found
   * PsiElement.
   *
   * @return the component name of the found element, or null if not (or
   *   more than one) found.
   */
  @Nullable
  public PsiElement resolveToComponentName() {
    final ResolveResult[] resolveResults = multiResolve(true, false);
    final PsiElement result = resolveResults.length == 0 ||
                              resolveResults.length > 1 ||
                              !resolveResults[0].isValidResult() ? null : resolveResults[0].getElement();

    if (result != null && result instanceof LogtalkNamedComponent) {
      return ((LogtalkNamedComponent)result).getComponentName();
    }

    return result;
  }

  /**
   * Resolve a reference, returning a list of possible candidates.
   *
   * @param incompleteCode Whether to treat the code as a fragment or not.
   *                       Usually, code is considered incomplete.
   * @return a (possibly empty) list of candidates that this reference matches.
   */
  @NotNull
  @Override
  public JavaResolveResult[] multiResolve(boolean incompleteCode) {
    return multiResolve(incompleteCode, true);
  }

  /**
   * Resolve this reference to a PsiElement -- *NOT* it's name.
   *
   * @param incompleteCode Whether to treat the code as a fragment or not.
   *                       Usually, code is considered incomplete.
   * @return the element this reference refers to, or null if none (or more
   *   than one) is found.
   */
  @Nullable
  public PsiElement resolve(boolean incompleteCode) {
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg(null));
    final ResolveResult[] resolveResults = multiResolve(incompleteCode);

    PsiElement resolved = resolveResults.length == 0 ||
                          resolveResults.length > 1 ||
                          !resolveResults[0].isValidResult() ? null : resolveResults[0].getElement();
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Resolved to " + (resolved != null ? resolved.toString() : "<null>")));
    return resolved;
  }

  @NotNull
  @Override
  public LogtalkClassResolveResult resolveLogtalkClass() {
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Begin resolving Logtalk class:" + this.getText()));
    LogtalkClassResolveResult result = resolveLogtalkClassInternal();
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Finished resolving Logtalk class " + this.getText() + " as " + result.debugDump()));
    return result;
  }

  @NotNull
  private LogtalkClassResolveResult resolveLogtalkClassInternal() {
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking for 'this'"));
    if (this instanceof LogtalkThisExpression) {
      LogtalkClass clazz = PsiTreeUtil.getParentOfType(this, LogtalkClass.class);
      // this has different semantics on abstracts
      if (clazz != null && clazz.getModel().isAbstract()) {
        LogtalkTypeOrAnonymous type = clazz.getModel().getUnderlyingType();
        if (type != null) {
          return LogtalkClassResolveResult.create(LogtalkResolveUtil.tryResolveClassByQName(type));
        }
      }
      return LogtalkClassResolveResult.create(clazz);
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking super class."));
    if (this instanceof LogtalkSuperExpression) {
      final LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(this, LogtalkClass.class);
      assert haxeClass != null;
      if (haxeClass.getLogtalkExtendsList().isEmpty()) {
        return LogtalkClassResolveResult.create(null);
      }
      final LogtalkExpression superExpression = haxeClass.getLogtalkExtendsList().get(0).getReferenceExpression();
      final LogtalkClassResolveResult superClassResolveResult = ((LogtalkReference)superExpression).resolveLogtalkClass();
      superClassResolveResult.specializeByParameters(haxeClass.getLogtalkExtendsList().get(0).getTypeParam());
      return superClassResolveResult;
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking string literal."));
    if (this instanceof LogtalkStringLiteralExpression) {
      return LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName("String", this));
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking literal."));
    if (this instanceof LogtalkLiteralExpression) {
      final PsiElement firstChild = getFirstChild();
      if (firstChild instanceof LeafPsiElement) {
        final LeafPsiElement child = (LeafPsiElement)getFirstChild();
        final IElementType childTokenType = child == null ? null : child.getElementType();
        return LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName(getLiteralClassName(childTokenType), this));
      }
      // Else, it's a block statement and not a named literal.
      return LogtalkClassResolveResult.create(null);
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking map literal."));
    if (this instanceof LogtalkMapLiteral) {
      return LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName("Map", this));
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking array literal."));
    if (this instanceof LogtalkArrayLiteral) {
      LogtalkArrayLiteral haxeArrayLiteral = (LogtalkArrayLiteral)this;
      LogtalkExpressionList expressionList = haxeArrayLiteral.getExpressionList();
      boolean isString = false;
      boolean sameClass = false;
      boolean implementOrExtendSameClass = false;
      LogtalkClass haxeClass = null;
      List<LogtalkType> commonTypeList = new ArrayList<>();
      List<LogtalkExpression> haxeExpressionList = expressionList != null ? expressionList.getExpressionList() : new ArrayList<>();
      if (!haxeExpressionList.isEmpty()) {
        isString = true;
        sameClass = true;

        for (LogtalkExpression expression : haxeExpressionList) {
          if (!(expression instanceof LogtalkStringLiteralExpression)) {
            isString = false;
          }

          if (sameClass || implementOrExtendSameClass) {
            LogtalkReferenceExpression haxeReference = null;
            if (expression instanceof LogtalkNewExpression || expression instanceof LogtalkCallExpression) {
              haxeReference = PsiTreeUtil.findChildOfType(expression, LogtalkReferenceExpression.class);
            }
            if (expression instanceof LogtalkReferenceExpression) {
              haxeReference = (LogtalkReferenceExpression)expression;
            }

            LogtalkClass haxeClassResolveResultLogtalkClass = null;
            if (haxeReference != null) {
              LogtalkClassResolveResult haxeClassResolveResult = haxeReference.resolveLogtalkClass();
              haxeClassResolveResultLogtalkClass = haxeClassResolveResult.getLogtalkClass();
              if (haxeClassResolveResultLogtalkClass != null) {
                if (haxeClass == null) {
                  haxeClass = haxeClassResolveResultLogtalkClass;
                  commonTypeList.addAll(haxeClass.getLogtalkImplementsList());
                  commonTypeList.addAll(haxeClass.getLogtalkExtendsList());
                }
              }
            }

            if (haxeClass != null && !haxeClass.equals(haxeClassResolveResultLogtalkClass)) {
              List<LogtalkType> haxeTypeList = new ArrayList<>();
              haxeTypeList.addAll(haxeClass.getLogtalkImplementsList());
              haxeTypeList.addAll(haxeClass.getLogtalkExtendsList());

              commonTypeList.retainAll(haxeTypeList);
              implementOrExtendSameClass = !commonTypeList.isEmpty();
            }

            if (haxeClass == null || !haxeClass.equals(haxeClassResolveResultLogtalkClass)) {
              sameClass = false;
            }
          }
        }

      }
      LogtalkClassResolveResult resolveResult =
        LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName(getLiteralClassName(getTokenType()), this));

      LogtalkClass resolveResultLogtalkClass = resolveResult.getLogtalkClass();

      LogtalkGenericSpecialization specialization = resolveResult.getSpecialization();
      if (resolveResultLogtalkClass != null &&
          specialization.get(resolveResultLogtalkClass, "T") == null) {  // TODO: 'T' should not be hard-coded.
        if (isString) {
          specialization.put(resolveResultLogtalkClass, "T", LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName("String", this)));
        } else if (sameClass) {
          specialization.put(resolveResultLogtalkClass, "T",
                             LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName(haxeClass.getQualifiedName(), this)));
        } else if (implementOrExtendSameClass) {
          LogtalkReferenceExpression haxeReferenceExpression = commonTypeList.get(commonTypeList.size() - 1).getReferenceExpression();
          LogtalkClassResolveResult resolveLogtalkClass = haxeReferenceExpression.resolveLogtalkClass();

          if (resolveLogtalkClass != LogtalkClassResolveResult.EMPTY) {
            LogtalkClass resolveLogtalkClassLogtalkClass = resolveLogtalkClass.getLogtalkClass();

            if (resolveLogtalkClassLogtalkClass != null) {
              specialization.put(resolveResultLogtalkClass, "T", LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName(
                resolveLogtalkClassLogtalkClass.getQualifiedName(), this)));
            }
          }
        }
      }

      return resolveResult;
    } // end (this instanceof LogtalkArrayLiteral)
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking 'new' expression."));
    if (this instanceof LogtalkNewExpression) {
      final LogtalkClassResolveResult result = LogtalkClassResolveResult.create(LogtalkResolveUtil.tryResolveClassByQName(
        ((LogtalkNewExpression)this).getType()));
      result.specialize(this);
      return result;
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking call expression."));
    if (this instanceof LogtalkCallExpression) {
      final LogtalkExpression expression = ((LogtalkCallExpression)this).getExpression();
      final LogtalkClassResolveResult leftResult = tryGetLeftResolveResult(expression);
      if (expression instanceof LogtalkReference) {
        final LogtalkClassResolveResult result =
          LogtalkResolveUtil.getLogtalkClassResolveResult(((LogtalkReference)expression).resolve(), leftResult.getSpecialization());
        result.specialize(this);
        return result;
      }
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Checking array access expression."));
    if (this instanceof LogtalkArrayAccessExpression) {
      // wrong generation. see LogtalkCallExpression
      final LogtalkReference reference = PsiTreeUtil.getChildOfType(this, LogtalkReference.class);
      if (reference != null) {
        final LogtalkClassResolveResult resolveResult = reference.resolveLogtalkClass();
        final LogtalkClass resolveResultLogtalkClass = resolveResult.getLogtalkClass();
        if (resolveResultLogtalkClass == null) {
          return resolveResult;
        }
        // std Array
        if ("Array".equals(resolveResultLogtalkClass.getQualifiedName())) {
          LogtalkClassResolveResult arrayResolveResult = resolveResult.getSpecialization().get(resolveResultLogtalkClass, "T");

          if (arrayResolveResult != null) {
            return arrayResolveResult;
          }
        }
        // @:arrayAccess methods, such as in Map or openfl.Vector
        return LogtalkResolveUtil.getLogtalkClassResolveResult(resolveResultLogtalkClass.findArrayAccessGetter(),
                                                         resolveResult.getSpecialization());
      }
    }

    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Calling resolve()"));
    PsiElement resolve = resolve();
    if (resolve instanceof PsiPackage) {
      // Packages don't ever resolve to classes. (And they don't have children!)
      return LogtalkClassResolveResult.EMPTY;
    }
    if (resolve != null) {
      PsiElement parent = resolve.getParent();

      if (parent != null) {
        if (parent instanceof LogtalkFunctionDeclarationWithAttributes || parent instanceof LogtalkExternFunctionDeclaration) {
          return LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName("Dynamic", this));
        }
        LogtalkTypeTag typeTag = PsiTreeUtil.getChildOfType(parent, LogtalkTypeTag.class);

        if (typeTag != null) {
          LogtalkFunctionType functionType = PsiTreeUtil.getChildOfType(typeTag, LogtalkFunctionType.class);
          if (functionType != null) {
            return LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName("Dynamic", this));
          }
        }
      }
    }

    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Trying class resolve with specialization."));
    LogtalkClassResolveResult result = LogtalkResolveUtil.getLogtalkClassResolveResult(resolve, tryGetLeftResolveResult(this).getSpecialization());
    if (result.getLogtalkClass() == null) {
      result = LogtalkClassResolveResult.create(LogtalkResolveUtil.findClassByQName(getText(), this));
    }
    return result;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    PsiElement element = this;
    final LogtalkIdentifier identifier = PsiTreeUtil.getChildOfType(element, LogtalkIdentifier.class);
    final LogtalkIdentifier identifierNew = LogtalkElementGenerator.createIdentifierFromText(getProject(), newElementName);

    if (identifier != null && identifierNew != null) {
      element.getNode().replaceChild(identifier.getNode(), identifierNew.getNode());
    }
    return this;
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    if (element instanceof LogtalkFile) {
      bindToFile(element);
    } else if (element instanceof PsiPackage) {
      bindToPackage((PsiPackage)element);
    } else if (element instanceof PsiClass) {
      bindToClass((PsiClass)element);
    }
    return this;
  }

  private void bindToClass(PsiClass element) {
    handleElementRename(element.getName());
  }

  private void bindToPackage(PsiPackage element) {
    final LogtalkImportStatement importStatement =
      LogtalkElementGenerator.createImportStatementFromPath(getProject(), element.getQualifiedName());
    LogtalkReferenceExpression referenceExpression = importStatement != null ? importStatement.getReferenceExpression() : null;
    if (referenceExpression == null) {
      LOG.error("ReferenceExpression generated by LogtalkElementGenerator is null!");
    } else {
      replace(referenceExpression);
    }
  }

  private void bindToFile(PsiElement element) {
    final LogtalkFileModel elementFileModel = LogtalkFileModel.fromElement(element);
    final LogtalkFileModel selfFileModel = LogtalkFileModel.fromElement(this);
    String destinationPackage = element.getUserData(LogtalkFileMoveHandler.destinationPackageKey);
    if (destinationPackage == null) {
      destinationPackage = "";
    }
    final String importPath = (destinationPackage.isEmpty() ? "" : destinationPackage + ".") +
                              elementFileModel.getName();

    if (resolve() == null) {
      final List<LogtalkImportModel> imports = selfFileModel.getImportModels();
      final boolean isInImportStatement = getParent() instanceof LogtalkImportStatement;
      final boolean isInQualifiedPath = getText().indexOf('.') != -1;
      final boolean inSamePackage = Objects.equals(selfFileModel.getPackageName(), elementFileModel.getPackageName());
      final boolean exposedByImports = imports.stream().anyMatch(
        model -> {
          return model.getExposedMembers().stream().anyMatch(item -> elementFileModel.getQualifiedInfo().equals(item.getQualifiedInfo()));
        });
      final boolean requiredToAddImport = !inSamePackage && (imports.isEmpty() || !exposedByImports);

      if (isInImportStatement) {
        if (inSamePackage || destinationPackage.isEmpty() || exposedByImports) {
          deleteImportStatement();
        } else {
          updateImportStatement(importPath);
        }
      } else if (isInQualifiedPath) {
        final String newName = destinationPackage.equals(selfFileModel.getPackageName()) ? elementFileModel.getName() : importPath;
        updateFullyQualifiedReference(newName);
      } else if (requiredToAddImport) {
        selfFileModel.addImport(importPath);
      }
    } else {
      final LogtalkImportStatement importStatement = selfFileModel.getImportStatements().stream()
        .filter(statement -> statement.getReferenceExpression() != null && importPath.equals(statement.getReferenceExpression().getText()))
        .findFirst()
        .orElse(null);

      if (importStatement != null) {
        importStatement.delete();
      }
    }
  }

  private void updateFullyQualifiedReference(String newName) {
    LogtalkReferenceExpression referenceExpression =
      LogtalkElementGenerator.createReferenceExpressionFromText(getProject(), newName);
    if (referenceExpression == null) {
      LOG.error("ReferenceExpression generated by LogtalkElementGenerator is null!");
    } else {
      replace(referenceExpression);
    }
  }

  private void updateImportStatement(String newImportPath) {
    LogtalkImportStatement importStatement = (LogtalkImportStatement)getParent();
    final LogtalkImportStatement newImportStatement = LogtalkElementGenerator.createImportStatementFromPath(getProject(), newImportPath);
    assert newImportStatement != null : "New import statement can't be null - LogtalkElementGenerator failed";
    importStatement.replace(newImportStatement);
  }

  private void deleteImportStatement() {
    if (getParent() instanceof LogtalkImportStatement) {
      try {
        getParent().delete();
      }
      catch (IncorrectOperationException ignored) {
      }
    }
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    // Resolving is (relatively) expensive, so if we're going to ignore the answer anyway, then don't bother.
    if (!(element instanceof LogtalkFile)) {
      final LogtalkReference[] references = PsiTreeUtil.getChildrenOfType(this, LogtalkReference.class);
      final boolean chain = references != null && references.length == 2;
      if (chain) return false;
    }
    final PsiElement resolve = element instanceof LogtalkComponentName ? resolveToComponentName() : resolve();
    if (element instanceof LogtalkFile && resolve instanceof LogtalkClass) {
      return element == resolve.getContainingFile();
    }
    return resolve == element;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final Set<LogtalkComponentName> suggestedVariants = new HashSet<>();
    final Set<LogtalkComponentName> suggestedVariantsExtensions = new HashSet<>();

    // if not first in chain
    // foo.bar.baz
    final LogtalkReference leftReference = LogtalkResolveUtil.getLeftReference(this);
    // TODO: This should use getName() instead of getQualifiedName(), but it isn't implemented properly and getName() NPEs.
    LogtalkClassResolveResult result = null;
    LogtalkClass haxeClass = null;
    String name = null;
    if (leftReference != null) {
      result = leftReference.resolveLogtalkClass();
      if (result != LogtalkClassResolveResult.EMPTY) {
        haxeClass = result.getLogtalkClass();
        if (haxeClass != null) {
          name = haxeClass.getName();
        }
      }
    }

    boolean isThis = leftReference instanceof LogtalkThisExpression;
    if (leftReference != null && name != null &&
        LogtalkResolveUtil.splitQName(leftReference.getText()).getSecond().equals(name)) {

      if (!isInUsingStatement() && !(isInImportStatement() && (haxeClass.isEnum() || haxeClass instanceof LogtalkAbstractClassDeclaration))) {
        addClassStaticMembersVariants(suggestedVariants, haxeClass, !(isThis));
      }

      addChildClassVariants(suggestedVariants, haxeClass);
    } else if (leftReference != null && !result.isFunctionType()) {
      if (null == haxeClass) {
        // TODO: fix haxeClass by type inference. Use compiler code assist?!
      }
      if (haxeClass != null) {
        boolean isSuper = leftReference instanceof LogtalkSuperExpression;
        PsiElement resolvedValue = leftReference.resolve();
        if (!isSuper && (resolvedValue instanceof LogtalkClassDeclaration ||
                         resolvedValue instanceof LogtalkAbstractClassDeclaration ||
                         resolvedValue instanceof LogtalkInterfaceDeclaration ||
                         resolvedValue instanceof LogtalkExternClassDeclaration)) {
          List<LogtalkModel> models = LogtalkProjectModel.fromElement(this).resolve(new FullyQualifiedInfo("", "Class", null, null));
          if (models != null && !models.isEmpty() && models.get(0) instanceof LogtalkClassModel) {
            haxeClass = ((LogtalkClassModel)models.get(0)).haxeClass;
          } else {
            haxeClass = null;
          }
        }

        addClassNonStaticMembersVariants(suggestedVariants, haxeClass,
                                         !(isThis || isSuper));
        addUsingVariants(suggestedVariants, suggestedVariantsExtensions, haxeClass, this);
      }
    } else {
      if (leftReference == null) {
        final boolean isElementInForwardMeta = LogtalkAbstractForwardUtil.isElementInForwardMeta(this);
        if (isElementInForwardMeta) {
          addAbstractUnderlyingClassVariants(suggestedVariants, PsiTreeUtil.getParentOfType(this, LogtalkClass.class));
        } else {
          PsiTreeUtil.treeWalkUp(new ComponentNameScopeProcessor(suggestedVariants), this, null, new ResolveState());
          addClassVariants(suggestedVariants, PsiTreeUtil.getParentOfType(this, LogtalkClass.class), false);
        }
      }
    }

    Object[] variants = LogtalkLookupElement.convert(result, suggestedVariants, suggestedVariantsExtensions).toArray();
    PsiElement leftTarget = leftReference != null ? leftReference.resolve() : null;

    if (leftTarget instanceof PsiPackage) {
      return ArrayUtil.mergeArrays(variants, ((PsiPackage)leftTarget).getSubPackages());
    } else if (leftTarget instanceof LogtalkFile) {
      return ArrayUtil.mergeArrays(variants, ((LogtalkFile)leftTarget).getClasses());
    } else if (leftReference == null) {
      PsiPackage rootPackage = JavaPsiFacade.getInstance(getElement().getProject()).findPackage("");
      return rootPackage == null ? variants : ArrayUtil.mergeArrays(variants, rootPackage.getSubPackages());
    }
    return variants;
  }

  private boolean isInUsingStatement() {
    return UsefulPsiTreeUtil.getParentOfType(this, LogtalkUsingStatement.class) != null;
  }

  private boolean isInImportStatement() {
    return UsefulPsiTreeUtil.getParentOfType(this, LogtalkImportStatement.class) != null;
  }

  private void addChildClassVariants(Set<LogtalkComponentName> variants, LogtalkClass haxeClass) {
    if (haxeClass != null) {
      PsiFile psiFile = haxeClass.getContainingFile();
      VirtualFile virtualFile = psiFile.getVirtualFile();

      if (virtualFile != null) {
        String nameWithoutExtension = virtualFile.getNameWithoutExtension();

        String name = haxeClass.getName();
        if (name != null && name.equals(nameWithoutExtension)) {
          List<LogtalkClass> haxeClassList = LogtalkResolveUtil.findComponentDeclarations(psiFile);

          for (LogtalkClass aClass : haxeClassList) {
            if (!aClass.getName().equals(nameWithoutExtension)) {
              variants.add(aClass.getComponentName());
            }
          }
        }
      }
    }
  }

  @NotNull
  private static LogtalkClassResolveResult tryGetLeftResolveResult(LogtalkExpression expression) {
    final LogtalkReference leftReference = PsiTreeUtil.getChildOfType(expression, LogtalkReference.class);
    return leftReference != null
           ? leftReference.resolveLogtalkClass()
           : LogtalkClassResolveResult.create(PsiTreeUtil.getParentOfType(expression, LogtalkClass.class));
  }

  @Nullable
  private static String getLiteralClassName(IElementType type) {
    if (type == LogtalkTokenTypes.STRING_LITERAL_EXPRESSION) {
      return "String";
    } else if (type == LogtalkTokenTypes.ARRAY_LITERAL) {
      return "Array";
    } else if (type == LogtalkTokenTypes.LITFLOAT) {
      return "Float";
    } else if (type == LogtalkTokenTypes.REG_EXP) {
      return "EReg";
    } else if (type == LogtalkTokenTypes.LITHEX || type == LogtalkTokenTypes.LITINT || type == LogtalkTokenTypes.LITOCT) {
      return "Int";
    }
    return null;
  }

  @NotNull
  private static JavaResolveResult[] toCandidateInfoArray(List<? extends PsiElement> elements) {
    final JavaResolveResult[] result = new JavaResolveResult[elements.size()];
    for (int i = 0, size = elements.size(); i < size; i++) {
      result[i] = new CandidateInfo(elements.get(i), EmptySubstitutor.getInstance());
    }
    return result;
  }

  private static void addUsingVariants(Set<LogtalkComponentName> variants,
                                       Set<LogtalkComponentName> variantsWithExtension,
                                       final @Nullable LogtalkClass ourClass,
                                       LogtalkReferenceImpl reference) {

    if (ourClass == null) return;

    LogtalkFileModel.fromElement(reference).getUsingModels().stream()
      .flatMap(model -> model.getExtensionMethods(ourClass).stream())
      .map(LogtalkMemberModel::getNamePsi)
      .forEach(name -> {
        variants.add(name);
        variantsWithExtension.add(name);
      });
  }

  private static void addClassVariants(Set<LogtalkComponentName> suggestedVariants, @Nullable LogtalkClass haxeClass, boolean filterByAccess) {
    if (haxeClass == null) {
      return;
    }
    for (LogtalkNamedComponent namedComponent : LogtalkResolveUtil.findNamedSubComponents(haxeClass)) {
      final boolean needFilter = filterByAccess && !namedComponent.isPublic();
      if (!needFilter && namedComponent.getComponentName() != null) {
        suggestedVariants.add(namedComponent.getComponentName());
      }
    }
  }

  private static void addAbstractUnderlyingClassVariants(Set<LogtalkComponentName> suggestedVariants,
                                                         @Nullable LogtalkClass haxeClass) {
    if (haxeClass == null || !haxeClass.isAbstract()) return;

    final LogtalkAbstractClassModel model = (LogtalkAbstractClassModel)haxeClass.getModel();
    final LogtalkClass underlyingClass = model.getUnderlyingClass();
    if (underlyingClass != null) {
      addClassVariants(suggestedVariants, underlyingClass, true);
    }
  }

  private static void addClassStaticMembersVariants(@NotNull final Set<LogtalkComponentName> suggestedVariants,
                                                    @NotNull final LogtalkClass haxeClass,
                                                    boolean filterByAccess) {

    final boolean isEnum = haxeClass.isEnum();

    List<LogtalkComponentName> staticMembers = haxeClass.getModel().getMembersSelf().stream()
      .filter(member -> (isEnum && member instanceof LogtalkEnumValueModel) || member.isStatic())
      .filter(member -> !filterByAccess || member.isPublic())
      .map(LogtalkMemberModel::getNamePsi)
      .collect(Collectors.toList());

    suggestedVariants.addAll(staticMembers);
  }

  private static void addClassNonStaticMembersVariants(Set<LogtalkComponentName> suggestedVariants,
                                                       @Nullable LogtalkClass haxeClass,
                                                       boolean filterByAccess) {
    if (haxeClass == null) {
      return;
    }

    LogtalkClassModel classModel = haxeClass.getModel();

    boolean extern = haxeClass.isExtern();
    boolean isAbstractEnum = haxeClass.isAbstract() && haxeClass.isEnum();
    boolean isAbstractForward = haxeClass.isAbstract() && ((LogtalkAbstractClassModel)classModel).hasForwards();

    if (isAbstractForward) {
      final List<LogtalkNamedComponent> forwardingLogtalkNamedComponents =
        LogtalkAbstractForwardUtil.findAbstractForwardingNamedSubComponents(haxeClass);
      if (forwardingLogtalkNamedComponents != null) {
        for (LogtalkNamedComponent namedComponent : forwardingLogtalkNamedComponents) {
          final boolean needFilter = filterByAccess && !namedComponent.isPublic();
          if ((extern || !needFilter) &&
              !namedComponent.isStatic() &&
              namedComponent.getComponentName() != null &&
              !isConstructor(namedComponent)) {
            suggestedVariants.add(namedComponent.getComponentName());
          }
        }
      }
    }

    for (LogtalkNamedComponent namedComponent : LogtalkResolveUtil.findNamedSubComponents(haxeClass)) {
      final boolean needFilter = filterByAccess && !namedComponent.isPublic();
      if (isAbstractEnum && LogtalkAbstractEnumUtil.couldBeAbstractEnumField(namedComponent)) {
        continue;
      }
      if ((extern || !needFilter) &&
          !namedComponent.isStatic() &&
          namedComponent.getComponentName() != null &&
          !isConstructor(namedComponent)) {
        suggestedVariants.add(namedComponent.getComponentName());
      }
    }
  }

  private static boolean isConstructor(LogtalkNamedComponent component) {
    return component instanceof LogtalkMethodPsiMixin && ((LogtalkMethodPsiMixin)component).isConstructor();
  }

  /* Determine if the element to the right of the given element in the AST
   * (at the same level) is a dot '.' separator.
   * Workhorse for getQualifier().
   * XXX: If we use this more than once, move it to a utility class, such as UsefulPsiTreeUtil.
   */
  private static boolean nextSiblingIsADot(PsiElement element) {
    if (null == element) return false;

    PsiElement next = element.getNextSibling();
    ASTNode node = ((null != next) ? next.getNode() : null);
    IElementType type = ((null != node) ? node.getElementType() : null);
    boolean ret = (null != type && type.equals(LogtalkTokenTypes.ODOT));
    return ret;
  }

  @Nullable
  @Override
  public PsiElement getReferenceNameElement() {
    PsiElement name = findChildByType(LogtalkTokenTypes.IDENTIFIER);
    if (name == null) name = getLastChild();
    return name;
  }

  @Nullable
  @Override
  public PsiReferenceParameterList getParameterList() {
    // TODO:  Unimplemented.
    LOG.warn("getParameterList is unimplemented");

    // REFERENCE_PARAMETER_LIST  in Java
    LogtalkTypeParam child = (LogtalkTypeParam)findChildByType(LogtalkTokenTypes.TYPE_PARAM);
    //return child == null ? null : child.getTypeList();
    return null;
  }

  @NotNull
  @Override
  public PsiType[] getTypeParameters() {
    // TODO:  Unimplemented.
    LOG.warn("getTypeParameters is unimplemented");
    return new PsiType[0];
  }

  @Override
  public boolean isQualified() {
    return null != getQualifier();
  }

  @Override
  public String getQualifiedName() {
    return JavaSourceUtil.getReferenceText(this);
  }

  @Override
  public void processVariants(@NotNull PsiScopeProcessor processor) {
    // TODO:  Unimplemented.
    LOG.warn("processVariants is unimplemented");
  }

  @Nullable
  @Override
  public PsiElement getQualifier() {
    PsiElement expression = getFirstChild();
    return expression != null && expression instanceof LogtalkReference ? expression : null;
  }

  @Nullable
  @Override
  public String getReferenceName() {
    PsiElement nameElement = getReferenceNameElement();
    return nameElement == null ? getText() : nameElement.getText();
  }

  // PsiExpression implementations

  @Nullable
  public PsiType getPsiType() {
    // XXX: EMB: Not sure about this.  Does a reference really have a sub-node giving the type?
    LogtalkType ht = findChildByClass(LogtalkType.class);
    return ((null == ht) ? null : ht.getPsiType());
  }

  // PsiExpression implementations

  //@Nullable
  //public PsiExpression getQualifierExpression() {
  //  final PsiElement qualifier = getQualifier();
  //  return qualifier instanceof PsiExpression ? (PsiExpression)qualifier : null;
  //}

  @Override
  public String toString() {
    String ss = super.toString();
    if (!ApplicationManager.getApplication().isUnitTestMode()) {
      // Unit tests don't want the extra data.  (Maybe we should fix the goldens?)
      String clazzName = this.getClass().getSimpleName();
      String text = getCanonicalText();
      ss += ":" + defaultIfEmpty(text, "<no text>");
      ss += ":" + defaultIfEmpty(clazzName, "<anonymous>");
    }
    return ss;
  }

  public String traceMsg(String message) {
    StringBuilder msg = new StringBuilder();
    String nodeText = this.getText();
    msg.append(null != nodeText ? nodeText : "<no text>");
    msg.append(':');
    if (null != message) {
      msg.append(message);
    }
    return LogtalkDebugUtil.traceMessage(msg.toString(), 2048);
  }

  // LogtalkLiteralExpression
  @Nullable
  @Override
  public LogtalkBlockStatement getBlockStatement() {
    return LogtalkStatementUtils.getBlockStatement(this);
  }
}
