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
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.LogtalkEnumValueModel;
import com.intellij.plugins.haxe.model.LogtalkFieldModel;
import com.intellij.plugins.haxe.model.LogtalkModel;
import com.intellij.plugins.haxe.util.LogtalkAbstractEnumUtil;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkModifierList;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiField;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiModifier;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;

/**
 * Created by srikanthg on 10/9/14.
 */
public abstract class LogtalkPsiFieldImpl extends AbstractLogtalkNamedComponent implements LogtalkPsiField {

  private static final Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.lang.psi.impl.LogtalkPsiFieldImpl");

  static {
    LOG.info("Loaded LogtalkPsiFieldImpl");
    LOG.setLevel(Level.DEBUG);
  }

  public LogtalkPsiFieldImpl(ASTNode node) {
    super(node);
  }

  @Override
  public LogtalkModel getModel() {
    if (this instanceof LogtalkEnumValueDeclaration) {
      return new LogtalkEnumValueModel((LogtalkEnumValueDeclaration)this);
    }
    if (LogtalkAbstractEnumUtil.isAbstractEnum(getContainingClass()) && LogtalkAbstractEnumUtil.couldBeAbstractEnumField(this)) {
      return new LogtalkEnumValueModel((LogtalkVarDeclaration)this);
    }
    return new LogtalkFieldModel(this);
  }

  @Override
  @Nullable
  @NonNls
  public String getName() {
    String name = super.getName();
    if (null == name) {
      final PsiIdentifier nameIdentifier = getNameIdentifier();
      if (nameIdentifier != null) {
        name = nameIdentifier.getText();
      }
    }

    return (name != null) ? name : "<unnamed>";
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    final LogtalkComponentName componentName = getComponentName();
    if (componentName != null) {
      componentName.setName(name);
    }
    return this;
  }

  @Override
  @Nullable
  public LogtalkComponentName getComponentName() {
    final PsiIdentifier identifier = getNameIdentifier();
    return identifier != null ? new LogtalkComponentNameImpl(getNode()) : null;
  }

  @Nullable
  @Override
  public PsiIdentifier getNameIdentifier() {
    final LogtalkComponentName compName = PsiTreeUtil.getChildOfType(this, LogtalkComponentName.class);
    return compName != null ? PsiTreeUtil.getChildOfType(compName, LogtalkIdentifier.class) : null;
  }

  @Nullable
  @Override
  public PsiDocComment getDocComment() {
    // TODO:  Implement 'public PsiDocComment getDocComment()'
    //PsiComment psiComment = LogtalkResolveUtil.findDocumentation(this);
    //return ((psiComment != null)? new LogtalkPsiDocComment(getDelegate(), psiComment) : null);
    return null;
  }

  private boolean isPrivate() {
    // TODO:  Implement 'private boolean isPrivate()'
    //final List<LogtalkDeclarationAttribute> declarationAttributeList = getDeclarationAttributeList();
    //for (LogtalkDeclarationAttribute declarationAttribute : declarationAttributeList) {
    //  LogtalkAccess access = declarationAttribute.getAccess();
    //  if (access!=null && "private".equals(access.getText())) {
    //    return true;
    //  }
    //}
    return false;
  }

  @Override
  public boolean isPublic() {
    return (!isPrivate() && super.isPublic()); // do not change the order of- and the- expressions
  }

  @Override
  public boolean isDeprecated() {
    return false;
  }

  @Override
  public void setInitializer(@Nullable PsiExpression initializer) throws IncorrectOperationException {
    // XXX: this may need to be implemented for refactoring functionality
  }

  @Nullable
  @Override
  public PsiClass getContainingClass() {
    return PsiTreeUtil.getParentOfType(this, LogtalkClass.class, true);
  }

  @NotNull
  @Override
  public PsiType getType() {
    PsiType psiType = null;
    final LogtalkTypeTag tag = PsiTreeUtil.getChildOfType(this, LogtalkTypeTag.class);
    if (tag != null) {
      final LogtalkTypeOrAnonymous toa = getFirstItem(tag.getTypeOrAnonymousList());
      final LogtalkType type = (toa != null) ? toa.getType() : null;
      psiType = (type != null) ? type.getPsiType() : null;
    }
    return psiType != null ? psiType : LogtalkPsiTypeAdapter.DYNAMIC;
  }

  @Nullable
  @Override
  public PsiTypeElement getTypeElement() {
    // Lifted, lock, stock, and barrel from PsiParameterImpl.java
    // which was for the Java language.
    // TODO:  Need to verify against the Logtalk language spec.
    //              Are there other situations?
    for (PsiElement child = getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child instanceof PsiTypeElement) {
        //noinspection unchecked
        return (PsiTypeElement)child;
      }
    }
    return null;
  }

  @Nullable
  @Override
  public PsiExpression getInitializer() {
    // XXX: this may need to be implemented for refactoring functionality
    return null;
  }

  @Override
  public boolean hasInitializer() {
    // XXX: this may need to be implemented for refactoring functionality
    return false;
  }

  @Override
  public void normalizeDeclaration() throws IncorrectOperationException {
    // intentionally left empty
  }

  @Nullable
  @Override
  public Object computeConstantValue() {
    return null;
  }

  @NotNull
  @Override
  public LogtalkModifierList getModifierList() {

    LogtalkModifierList list = super.getModifierList();

    if (null == list) {
      list = new LogtalkModifierListImpl(this.getNode());
    }

    // -- below modifiers need to be set individually
    //    because, they cannot be enforced through macro-list

    if (isStatic()) {
      list.setModifierProperty(LogtalkPsiModifier.STATIC, true);
    }

    if (isInline()) {
      list.setModifierProperty(LogtalkPsiModifier.INLINE, true);
    }

    if (isPublic()) {
      list.setModifierProperty(LogtalkPsiModifier.PUBLIC, true);
    } else {
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
  public SearchScope getUseScope() {
    final PsiElement localVar = UsefulPsiTreeUtil.getParentOfType(this, LogtalkLocalVarDeclaration.class);
    if (localVar != null) {
      final PsiElement outerBlock = UsefulPsiTreeUtil.getParentOfType(localVar, LogtalkBlockStatement.class);
      if (outerBlock != null) {
        return new LocalSearchScope(outerBlock);
      }
    }
    return super.getUseScope();
  }
}
