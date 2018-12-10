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
package com.intellij.plugins.logtalk.lang.psi;

import com.intellij.openapi.util.Key;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.model.*;
import com.intellij.plugins.haxe.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkResolver implements ResolveCache.AbstractResolver<LogtalkReference, List<? extends PsiElement>> {
  private static LogtalkDebugLogger LOG = LogtalkDebugLogger.getLogger();

  //static {  // Remove when finished debugging.
  //  LOG.setLevel(Level.TRACE);
  //  LOG.debug(" ========= Starting up debug logger for LogtalkResolver. ==========");
  //}

  public static final LogtalkResolver INSTANCE = new LogtalkResolver();

  public static ThreadLocal<Boolean> isExtension = new ThreadLocal<>();

  @Override
  public List<? extends PsiElement> resolve(@NotNull LogtalkReference reference, boolean incompleteCode) {
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("-----------------------------------------"));
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Resolving reference: " + reference.getText()));

    isExtension.set(false);

    List<? extends PsiElement> result = checkIsType(reference);
    if (result == null) result = checkIsFullyQualifiedStatement(reference);
    if (result == null) result = checkIsSuperExpression(reference);
    if (result == null) result = checkIsClassName(reference);
    if (result == null) result = checkIsChain(reference);
    if (result == null) result = checkIsAccessor(reference);
    if (result == null) result = checkByTreeWalk(reference);
    if (result == null) {
      // try super field
      List<? extends PsiElement> superElements =
        resolveByClassAndSymbol(PsiTreeUtil.getParentOfType(reference, LogtalkClass.class), reference);
      if (!superElements.isEmpty()) {
        LogResolution(reference, "via super field.");
        return superElements;
      }

      LogtalkFileModel fileModel = LogtalkFileModel.fromElement(reference);
      if (fileModel != null) {
        String className = reference.getText();

        PsiElement target = LogtalkResolveUtil.searchInSameFile(fileModel, className);
        if (target == null) target = LogtalkResolveUtil.searchInImports(fileModel, className);
        if (target == null) target = LogtalkResolveUtil.searchInSamePackage(fileModel, className);

        if (target != null) {
          return asList(target);
        }
      }

      LogResolution(reference, "failed after exhausting all options.");

      if (PsiNameHelper.getInstance(reference.getProject()).isQualifiedName(reference.getText())) {
        List<LogtalkModel> resolvedPackage =
          LogtalkProjectModel.fromElement(reference).resolve(new FullyQualifiedInfo(reference.getText()), reference.getResolveScope());
        if (resolvedPackage != null && !resolvedPackage.isEmpty() && resolvedPackage.get(0) instanceof LogtalkPackageModel) {
          LogResolution(reference, "via project qualified name.");
          return Collections.singletonList(resolvedPackage.get(0).getBasePsi());
        }
      }
    }

    return result == null ? ContainerUtil.emptyList() : result;
  }

  private List<? extends PsiElement> checkByTreeWalk(LogtalkReference reference) {
    final List<PsiElement> result = new ArrayList<>();
    PsiTreeUtil.treeWalkUp(new ResolveScopeProcessor(result, reference.getText()), reference, null, new ResolveState());
    if (result.isEmpty()) return null;
    LogResolution(reference, "via tree walk.");
    return result;
  }

  private List<? extends PsiElement> checkIsAccessor(LogtalkReference reference) {
    if (reference instanceof LogtalkPropertyAccessor) {
      final LogtalkAccessorType accessorType = LogtalkAccessorType.fromPsi(reference);
      if (accessorType != LogtalkAccessorType.GET && accessorType != LogtalkAccessorType.SET) return null;

      final LogtalkVarDeclaration varDeclaration = PsiTreeUtil.getParentOfType(reference, LogtalkVarDeclaration.class);
      if (varDeclaration == null) return null;

      final LogtalkFieldModel fieldModel = new LogtalkFieldModel(varDeclaration);
      final LogtalkMethodModel method = accessorType == LogtalkAccessorType.GET ? fieldModel.getGetterMethod() : fieldModel.getSetterMethod();

      if (method != null) {
        return asList(method.getBasePsi());
      }
    }

    return null;
  }

  @Nullable
  private List<? extends PsiElement> checkIsChain(@NotNull LogtalkReference reference) {
    final LogtalkReference leftReference = LogtalkResolveUtil.getLeftReference(reference);
    if (leftReference != null) {
      List<? extends PsiElement> result = resolveChain(leftReference, reference);
      if (result != null && !result.isEmpty()) {
        LogResolution(reference, "via simple chain using leftReference.");
        return result;
      }
      LogResolution(reference, "via simple chain against package.");
      PsiElement item = resolveQualifiedReference(reference);
      if (item != null) {
        return asList(item);
      }
    }
    return null;
  }

  @Nullable
  private List<? extends PsiElement> checkIsClassName(@NotNull LogtalkReference reference) {
    final LogtalkClass resultClass = LogtalkResolveUtil.tryResolveClassByQName(reference);
    if (resultClass != null) {
      LogResolution(reference, "via class qualified name.");
      return asList(resultClass.getComponentName());
    }
    return null;
  }

  @Nullable
  private List<? extends PsiElement> checkIsSuperExpression(LogtalkReference reference) {
    if (reference instanceof LogtalkSuperExpression && reference.getParent() instanceof LogtalkCallExpression) {
      final LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(reference, LogtalkClass.class);
      assert haxeClass != null;
      if (!haxeClass.getLogtalkExtendsList().isEmpty()) {
        final LogtalkExpression superExpression = haxeClass.getLogtalkExtendsList().get(0).getReferenceExpression();
        final LogtalkClass superClass = ((LogtalkReference)superExpression).resolveLogtalkClass().getLogtalkClass();
        final LogtalkNamedComponent constructor =
          ((superClass == null) ? null : superClass.findLogtalkMethodByName(LogtalkTokenTypes.ONEW.toString()));
        LogResolution(reference, "because it's a super expression.");
        return asList(((constructor != null) ? constructor : superClass));
      }
    }

    return null;
  }

  @Nullable
  private List<? extends PsiElement> checkIsType(LogtalkReference reference) {
    final LogtalkType type = PsiTreeUtil.getParentOfType(reference, LogtalkType.class);
    final LogtalkClass haxeClassInType = LogtalkResolveUtil.tryResolveClassByQName(type);
    if (type != null && haxeClassInType != null) {
      LogResolution(reference, "via parent type name.");
      return asList(haxeClassInType.getComponentName());
    }
    return null;
  }

  private List<? extends PsiElement> checkIsFullyQualifiedStatement(@NotNull LogtalkReference reference) {
    if (PsiTreeUtil.getParentOfType(reference,
                                    LogtalkPackageStatement.class,
                                    LogtalkImportStatement.class,
                                    LogtalkUsingStatement.class) != null && reference instanceof LogtalkReferenceExpression) {
      LogResolution(reference, "via parent/package import.");
      return asList(resolveQualifiedReference((LogtalkReferenceExpression)reference));
    }
    return null;
  }

  private void LogResolution(LogtalkReference ref, String tailmsg) {
    // Debug is always enabled if trace is enabled.
    if (LOG.isDebugEnabled()) {
      String message = "Resolved " + ref.getText() + " " + tailmsg;
      if (LOG.isTraceEnabled()) {
        LOG.traceAs(LogtalkDebugUtil.getCallerStackFrame(), message);
      } else {
        LOG.debug(message);
      }
    }
  }

  /**
   * Resolve a chain reference, given two references: the qualifier, and the name.
   *
   * @param lefthandExpression - qualifying expression (e.g. "((ref = reference).getProject())")
   * @param reference          - field/method name to resolve.
   * @return the resolved element, if found; null, otherwise.
   */
  @Nullable
  private List<? extends PsiElement> resolveChain(LogtalkReference lefthandExpression, LogtalkReference reference) {
    String identifier =
      reference instanceof LogtalkReferenceExpression ? ((LogtalkReferenceExpression)reference).getIdentifier().getText() : reference.getText();
    LogtalkClassResolveResult leftExpression = lefthandExpression.resolveLogtalkClass();
    if (leftExpression.getLogtalkClass() != null) {
      LogtalkMemberModel member = leftExpression.getLogtalkClass().getModel().getMember(identifier);
      if (member != null) {
        return Collections.singletonList(member.getBasePsi());
      }
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg(null));
    final LogtalkComponentName componentName = tryResolveHelperClass(lefthandExpression, identifier);
    if (componentName != null) {
      if (LOG.isTraceEnabled()) LOG.trace("Found component " + componentName.getText());
      return Collections.singletonList(componentName);
    }
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("trying keywords (super, new) arrays, literals, etc."));
    // Try resolving keywords (super, new), arrays, literals, etc.
    return resolveByClassAndSymbol(lefthandExpression.resolveLogtalkClass(), reference);
  }

  private PsiElement resolveQualifiedReference(LogtalkReference reference) {
    String qualifiedName = reference.getText();

    final FullyQualifiedInfo qualifiedInfo = new FullyQualifiedInfo(qualifiedName);
    List<LogtalkModel> result = LogtalkProjectModel.fromElement(reference).resolve(qualifiedInfo, reference.getResolveScope());
    if (result != null && !result.isEmpty()) {
      LogtalkModel item = result.get(0);
      if (item instanceof LogtalkFileModel) {
        LogtalkClassModel mainClass = ((LogtalkFileModel)item).getMainClassModel();
        if (mainClass != null) {
          return mainClass.getBasePsi();
        }
      }
      return item.getBasePsi();
    }

    return null;
  }

  /**
   * Test if the leftReference is a class name (either locally or in a super-class),
   * and if so, find the named field/method declared inside of it.
   *
   * @param leftReference - a potential class name.
   * @param helperName    - the field/method to find.
   * @return the name of the found field/method.  null if not found.
   */
  @Nullable
  private LogtalkComponentName tryResolveHelperClass(LogtalkReference leftReference, String helperName) {
    if (LOG.isTraceEnabled()) LOG.trace(traceMsg("leftReference=" + leftReference + " helperName=" + helperName));
    LogtalkComponentName componentName = null;
    LogtalkClass leftResultClass = LogtalkResolveUtil.tryResolveClassByQName(leftReference);
    if (leftResultClass != null) {
      if (LOG.isTraceEnabled()) {
        LOG.trace(traceMsg("Found a left result via QName: " + (leftResultClass.getText() != null ? leftResultClass : "<no text>")));
      }
      // helper reference via class com.bar.FooClass.HelperClass
      final LogtalkClass componentDeclaration =
        LogtalkResolveUtil.findComponentDeclaration(leftResultClass.getContainingFile(), helperName);
      componentName = componentDeclaration == null ? null : componentDeclaration.getComponentName();
    } else {
      // try to find component at abstract forwarding underlying class
      leftResultClass = leftReference.resolveLogtalkClass().getLogtalkClass();
      if (LOG.isTraceEnabled()) {
        String resultClassName = leftResultClass != null ? leftResultClass.getText() : null;
        if (LOG.isTraceEnabled()) {
          LOG.trace(traceMsg("Found abstract left result:" + (resultClassName != null ? resultClassName : "<no text>")));
        }
      }
      if (leftResultClass != null) {
        LogtalkClassModel model = leftResultClass.getModel();
        LogtalkMemberModel member = model.getMember(helperName);
        if (member != null) return member.getNamePsi();

        if (model.isAbstract() && ((LogtalkAbstractClassModel)model).hasForwards()) {
          final List<LogtalkNamedComponent> forwardingLogtalkNamedComponents =
            LogtalkAbstractForwardUtil.findAbstractForwardingNamedSubComponents(leftResultClass);
          if (forwardingLogtalkNamedComponents != null) {
            for (LogtalkNamedComponent namedComponent : forwardingLogtalkNamedComponents) {
              final LogtalkComponentName forwardingComponentName = namedComponent.getComponentName();
              if (forwardingComponentName != null && forwardingComponentName.getText().equals(helperName)) {
                componentName = forwardingComponentName;
                break;
              }
            }
          }
        }
      }
    }
    if (LOG.isTraceEnabled()) {
      String ctext = componentName != null ? componentName.getText() : null;
      if (LOG.isTraceEnabled()) LOG.trace(traceMsg("Found component name " + (ctext != null ? ctext : "<no text>")));
    }
    return componentName;
  }

  private static List<? extends PsiElement> asList(@Nullable PsiElement element) {
    if (LOG.isDebugEnabled()) LOG.debug("Resolved as " + (element == null ? "empty result list." : element.toString()));
    return element == null ? Collections.emptyList() : Collections.singletonList(element);
  }

  private static List<? extends PsiElement> resolveByClassAndSymbol(@Nullable LogtalkClassResolveResult resolveResult,
                                                                    @NotNull LogtalkReference reference) {
    if (resolveResult == null) {
      if (LOG.isDebugEnabled()) LOG.debug("Resolved as empty result list. (resolveByClassAndSymbol)");
    }
    return resolveResult == null ? Collections.<PsiElement>emptyList() : resolveByClassAndSymbol(resolveResult.getLogtalkClass(), reference);
  }

  private static List<? extends PsiElement> resolveByClassAndSymbol(@Nullable LogtalkClass leftClass, @NotNull LogtalkReference reference) {
    if (leftClass != null) {
      final LogtalkClassModel classModel = leftClass.getModel();
      LogtalkMemberModel member = classModel.getMember(reference.getReferenceName());
      if (member != null) return asList(member.getNamePsi());

      // if class is abstract try find in forwards
      if (leftClass.isAbstract()) {
        LogtalkAbstractClassModel model = (LogtalkAbstractClassModel)leftClass.getModel();
        if (model.hasForwards()) {
          final LogtalkClass underlyingClass = model.getUnderlyingClass();
          if (underlyingClass != null) {
            member = underlyingClass.getModel().getMember(reference.getReferenceName());
            if (member != null) {
              return asList(member.getNamePsi());
            }
          }
        }
      }
      // try find using
      LogtalkFileModel fileModel = LogtalkFileModel.fromElement(reference);
      if (fileModel != null) {
        for (LogtalkUsingModel model : fileModel.getUsingModels()) {
          LogtalkMethodModel method = model.findExtensionMethod(reference.getReferenceName(), leftClass);
          if (method != null) {
            isExtension.set(true);
            return asList(method.getNamePsi());
          }
        }
      }
    }

    return Collections.emptyList();
  }

  private String traceMsg(String msg) {
    return LogtalkDebugUtil.traceMessage(msg, 120);
  }

  private class ResolveScopeProcessor implements PsiScopeProcessor {
    private final List<PsiElement> result;
    final String name;

    private ResolveScopeProcessor(List<PsiElement> result, String name) {
      this.result = result;
      this.name = name;
    }

    @Override
    public boolean execute(@NotNull PsiElement element, ResolveState state) {
      LogtalkComponentName componentName = null;
      if (element instanceof LogtalkNamedComponent) {
        componentName = ((LogtalkNamedComponent)element).getComponentName();
      } else if (element instanceof LogtalkOpenParameterList) {
        componentName = ((LogtalkOpenParameterList)element).getComponentName();
      }
      if (componentName != null && name.equals(componentName.getText())) {
        result.add(componentName);
        return false;
      }
      return true;
    }

    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
      return null;
    }

    @Override
    public void handleEvent(Event event, @Nullable Object associated) {
    }
  }
}