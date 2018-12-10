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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.LogtalkMethodModel;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiImplUtil;
import com.intellij.psi.impl.PsiSuperMethodImplUtil;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;


/**
 * @author: Srikanth.Ganapavarapu
 */
public abstract class LogtalkMethodPsiMixinImpl extends AbstractLogtalkNamedComponent implements LogtalkMethodPsiMixin {

  private static final Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.lang.psi.impl.LogtalkMethodPsiMixinImpl");
  static {
    LOG.info("Loaded LogtalkMethodPsiMixinImpl");
    LOG.setLevel(Level.DEBUG);
  }

  public LogtalkMethodPsiMixinImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  @NonNls
  public String getName() {
    final String name = super.getName();

    if (name == null) {
      PsiElement child = this.getFirstChild();
      while (child != null) {
        if (child instanceof LogtalkPsiToken && child.getText().equals(LogtalkTokenTypes.ONEW.toString())) {
          return child.getText();
        }
        child = child.getNextSibling();
      }
    }

    return (name != null) ? name : "<unnamed>";
  }

  private LogtalkMethodModel _model = null;
  public LogtalkMethodModel getModel() {
    if (_model == null) _model = new LogtalkMethodModel(this);
    return _model;
  }

  @Nullable
  @Override
  public List<LogtalkDeclarationAttribute> getDeclarationAttributeList() {
    // Not all function types have one of these...  If they do, the
    // subclass (via the generator) will override this method.
    return null;
  }


  @Nullable
  public LogtalkReturnStatement getReturnStatement() {
    // Not all function types have one of these...  If they do, the
    // subclass (via the generator) will override this method.
    return findChildByClass(LogtalkReturnStatement.class);
  }


  @Nullable
  public LogtalkTypeTag getTypeTag() {
    // Not all function types have one of these...  If they do, the
    // subclass (via the generator) will override this method.
    return findChildByClass(LogtalkTypeTag.class);
  }


  @Nullable
  @Override
  public PsiType getReturnType() {
    if (isConstructor()) {
      return null;
    }
    // A LogtalkFunctionType is a PSI Element.
    // LogtalkFunctionType type = getTypeTag().getFunctionType(); // type could be null
    /* TODO: : 'public PsiType getReturnType()': translate above objects into PsiType */
    return null;
  }

  @Nullable
  @Override
  public PsiTypeElement getReturnTypeElement() {
    /* TODO: : 'public PsiType getReturnType()': translate below objects into PsiTypeElement */
    // return getTypeTag();
    return null;
  }


  @Nullable
  public LogtalkThrowStatement getThrowStatement() {
    // Not all function types have one of these...  If they do, the
    // subclass (via the generator) will override this method.
    return null;
  }

  @NotNull
  @Override
  public PsiReferenceList getThrowsList() {
    LogtalkThrowStatement ts = getThrowStatement();
    return new LogtalkPsiReferenceList(this.getContainingClass(),
                                    (ts == null ? new LogtalkDummyASTNode("ThrowsList") : ts.getNode()),
                                    null);
  }

  @Nullable
  public LogtalkBlockStatement getBlockStatement() {
    // Not all function types have one of these...  If they do, the
    // subclass (via the generator) will override this method.
    return null;
  }

  @Nullable
  @Override
  public PsiCodeBlock getBody() {
    LogtalkBlockStatement bs = getBlockStatement();
    if (bs == null) {
      return null;
    }

    return (PsiCodeBlock)bs.getNode().getPsi(PsiCodeBlock.class);
  }

  @Override
  public boolean isConstructor() {
    String name = getName();
    return name != null && name.equals(LogtalkTokenTypes.ONEW.toString());
  }

  @Nullable
  @Override
  public PsiDocComment getDocComment() {
    // TODO: Fix 'public PsiDocComment getDocComment()'
    //PsiComment psiComment = LogtalkResolveUtil.findDocumentation(this);
    //return ((psiComment != null)? new LogtalkPsiDocComment(this, psiComment) : null);
    return null;
  }

  @Override
  public boolean isVarArgs() {
    // In Logtalk, the method is set to VarArgs at runtime, via a function call.
    // We would need the ability to know if a particular run sequence has
    // called such a function.  I don't think we can pull that off without
    // the compiler's help.
    // TODO: Use compiler completion to detect variable arguments usage.
    return false;
  }

  @Override
  public boolean isDeprecated() {
    return false;
  }

  @Override
  public boolean hasTypeParameters() {
    return PsiImplUtil.hasTypeParameters(this);
  }

  @NotNull
  @Override
  public PsiTypeParameter[] getTypeParameters() {
    // Type parameters are those inside of the type designation (e.g.
    // inside the '<' and '>').
    return PsiImplUtil.getTypeParameters(this);
  }

  @Nullable
  @Override
  public PsiClass getContainingClass() {
    return PsiTreeUtil.getParentOfType(this, LogtalkClass.class, true);
  }

  @NotNull
  @Override
  public MethodSignature getSignature(@NotNull PsiSubstitutor substitutor) {
    // XXX: PsiMethod uses a cache for substitutors.
    return MethodSignatureBackedByPsiMethod.create(this, substitutor);
  }

  @Nullable
  @Override
  public PsiIdentifier getNameIdentifier() {
    final LogtalkComponentName componentName = getComponentName();
    return componentName != null ? componentName.getIdentifier() : null;
  }

  @NotNull
  @Override
  public PsiMethod[] findSuperMethods() {
    return LogtalkMethodUtils.findSuperMethods(this);
  }

  @NotNull
  @Override
  public PsiMethod[] findSuperMethods(boolean checkAccess) {
    return LogtalkMethodUtils.findSuperMethods(this);
  }

  @NotNull
  @Override
  public PsiMethod[] findSuperMethods(PsiClass parentClass) {
    return LogtalkMethodUtils.findSuperMethods(this, parentClass);
  }

  @NotNull
  @Override
  public List<MethodSignatureBackedByPsiMethod> findSuperMethodSignaturesIncludingStatic(boolean checkAccess) {
    return LogtalkMethodUtils.findSuperMethodSignaturesIncludingStatic(this);
  }

  @Deprecated
  @Nullable
  @Override
  public PsiMethod findDeepestSuperMethod() {
    return LogtalkMethodUtils.findDeepestSuperMethod(this);
  }

  @NotNull
  @Override
  public PsiMethod[] findDeepestSuperMethods() {
    return LogtalkMethodUtils.findDeepestSuperMethods(this);
  }

  @Nullable
  @Override
  public PsiTypeParameterList getTypeParameterList() {
    // Type parameters are those inside of the type designation (e.g. inside the '<' and '>').
    LogtalkTypeParam               param   = null;
    final LogtalkTypeTag           tag     = (LogtalkTypeTag) findChildByType(LogtalkTokenTypes.TYPE_TAG);
    if (tag != null) {
      final LogtalkTypeOrAnonymous toa     = getFirstItem(tag.getTypeOrAnonymousList());
      final LogtalkType            type    = (toa != null) ? toa.getType() : null;
      param                             = (type != null) ? type.getTypeParam() : null;// XXX: Java<->Logtalk list & type inversion -- See BNF.
    }
    return param;
  }


  @NotNull
  @Override
  public LogtalkModifierList getModifierList() {

    //
    // Note Logtalk's rules for visibility:
    // (from http://haxe.org/manual/class-field-visibility.html)
    //
    // Omitting the visibility modifier usually defaults the visibility to private,
    // but there are exceptions where it becomes public instead:
    //
    // - If the class is declared as extern.
    // - If the field id declared on an interface.
    // - If the field overrides a public field.
    //
    // Trivia: Protected
    //
    //   Logtalk has no notion of a protected keyword known from Java, C++ and
    //   other object-oriented languages. However, its private behavior is
    //   equal to those language's protected behavior, so Logtalk actually
    //   lacks their real private behavior.
    //

    LogtalkModifierList list = super.getModifierList();

    if (null == list) {
      list = new LogtalkModifierListImpl(this.getNode());
    }

    // -- below modifiers need to be set individually
    //    because, they cannot be enforced through macro-list

    if (super.isStatic()) {
      list.setModifierProperty(LogtalkPsiModifier.STATIC, true);
    }

    if (super.isPublic()) {
      list.setModifierProperty(LogtalkPsiModifier.PUBLIC, true);
    }
    else {
      list.setModifierProperty(LogtalkPsiModifier.PRIVATE, true);
    }

    return list;
  }

  @Override
  public boolean hasModifierProperty(@LogtalkPsiModifier.ModifierConstant @NonNls @NotNull String name) {
    return this.getModifierList().hasModifierProperty(name);
  }

  @NotNull
  @Override
  public HierarchicalMethodSignature getHierarchicalMethodSignature() {
    return PsiSuperMethodImplUtil.getHierarchicalMethodSignature(this);
  }

  @NotNull
  @Override
  public PsiParameterList getParameterList() {
    final LogtalkParameterList list = PsiTreeUtil.getChildOfType(this, LogtalkParameterList.class);
    return ((list != null) ? list : new LogtalkParameterListImpl(new LogtalkDummyASTNode("Dummy parameter list")));
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
    if(this instanceof LogtalkLocalFunctionDeclaration) {
      final PsiElement outerBlock = UsefulPsiTreeUtil.getParentOfType(this, LogtalkBlockStatement.class);
      if(outerBlock != null) {
        return new LocalSearchScope(outerBlock);
      }
    }
    return super.getUseScope();
  }
}
