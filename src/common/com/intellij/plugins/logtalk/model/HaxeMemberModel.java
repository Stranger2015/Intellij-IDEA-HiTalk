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
package com.intellij.plugins.logtalk.model;

import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.haxe.model.type.LogtalkTypeResolver;
import com.intellij.plugins.haxe.model.type.ResultHolder;
import com.intellij.plugins.haxe.util.LogtalkAbstractEnumUtil;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkMethod;
import com.intellij.plugins.logtalk.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.logtalk.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.logtalk.util.LogtalkAbstractEnumUtil;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.plugins.haxe.model.LogtalkModifierType.*;

abstract public class LogtalkMemberModel implements LogtalkModel {
  private PsiElement basePsi;

  public LogtalkMemberModel(PsiElement basePsi) {
    this.basePsi = basePsi;
  }

  @Override
  public PsiElement getBasePsi() {
    return basePsi;
  }

  public boolean isPublic() {
    LogtalkClassModel declaringClass = getDeclaringClass();

    return hasModifier(PUBLIC)
           // Fields and methods of externs and interfaces are public by default, private modifier for them should be defined explicitly
           || ((declaringClass.isInterface() || declaringClass.isExtern()) && !hasModifier(PRIVATE))
           || isOverriddenPublicMethod()
           || getDeclaringClass().hasMeta("@:publicFields");
  }

  private boolean isOverriddenPublicMethod() {
    if (hasModifier(OVERRIDE)) {
      final LogtalkMemberModel parentMember = getParentMember();
      return parentMember != null && parentMember.isPublic();
    }

    return false;
  }

  public boolean hasModifier(LogtalkModifierType aPublic) {
    return this.getModifiers().hasModifier(aPublic);
  }

  public boolean isStatic() {
    return hasModifier(STATIC);
  }

  private LogtalkDocumentModel _document = null;

  @NotNull
  public LogtalkDocumentModel getDocument() {
    if (_document == null) _document = new LogtalkDocumentModel(this.getBasePsi());
    return _document;
  }

  public LogtalkNamedComponent getNamedComponentPsi() {
    return getNamedComponentPsi(basePsi);
  }

  static private LogtalkNamedComponent getNamedComponentPsi(PsiElement element) {
    if (element == null) return null;
    if (element instanceof LogtalkNamedComponent) return (LogtalkNamedComponent)element;
    if (element.getParent() instanceof LogtalkNamedComponent) return (LogtalkNamedComponent)element.getParent();
    return getNamedComponentPsi(UsefulPsiTreeUtil.getChild(element, LogtalkNamedComponent.class));
  }

  public String getName() {
    LogtalkComponentName namePsi = getNamePsi();
    return namePsi == null ? "" : namePsi.getText();
  }

  public LogtalkComponentName getNamePsi() {
    LogtalkComponentName componentName = UsefulPsiTreeUtil.getChild(basePsi, LogtalkComponentName.class);
    if (componentName != null && componentName.getParent() instanceof LogtalkNamedComponent) {
      return componentName;
    }
    return null;
  }

  @NotNull
  public PsiElement getNameOrBasePsi() {
    PsiElement element = getNamePsi();
    if (element == null) element = getBasePsi();
    return element;
  }

  abstract public LogtalkClassModel getDeclaringClass();

  private LogtalkModifiersModel _modifiers;

  @NotNull
  public LogtalkModifiersModel getModifiers() {
    if (_modifiers == null) _modifiers = new LogtalkModifiersModel(basePsi);
    return _modifiers;
  }

  public static LogtalkMemberModel fromPsi(PsiElement element) {
    if (element instanceof LogtalkMethod) return ((LogtalkMethod)element).getModel();
    if (element instanceof LogtalkVarDeclaration) {
      PsiClass containingClass = ((LogtalkVarDeclaration)element).getContainingClass();
      if (LogtalkAbstractEnumUtil.isAbstractEnum(containingClass) && LogtalkAbstractEnumUtil.couldBeAbstractEnumField(element)) {
        return new LogtalkEnumValueModel((LogtalkVarDeclaration)element);
      }
      return new LogtalkFieldModel((LogtalkVarDeclaration)element);
    }
    if (element instanceof LogtalkEnumValueDeclaration) return new LogtalkEnumValueModel((LogtalkEnumValueDeclaration)element);
    if (element instanceof LogtalkLocalVarDeclaration) return new LogtalkLocalVarModel((LogtalkLocalVarDeclaration)element);
    if (element instanceof LogtalkParameter) return new LogtalkParameterModel((LogtalkParameter)element);
    if (element instanceof LogtalkForStatement) return null;
    final PsiElement parent = element.getParent();
    return (parent != null) ? fromPsi(parent) : null;
  }

  public ResultHolder getResultType() {
    return LogtalkTypeResolver.getFieldOrMethodReturnType((AbstractLogtalkNamedComponent)this.basePsi);
  }

  public String getPresentableText(LogtalkMethodContext context) {
    return this.getName() + ":" + getResultType();
  }

  public LogtalkMemberModel getParentMember() {
    final LogtalkClassModel aClass = getDeclaringClass().getParentClass();
    return (aClass != null) ? aClass.getMember(this.getName()) : null;
  }

  @Nullable
  @Override
  public FullyQualifiedInfo getQualifiedInfo() {
    if (getDeclaringClass() != null && isStatic() && isPublic()) {
      FullyQualifiedInfo containerInfo = getDeclaringClass().getQualifiedInfo();
      if (containerInfo != null) {
        return new FullyQualifiedInfo(containerInfo.packagePath, containerInfo.fileName, containerInfo.className, getName());
      }
    }
    return null;
  }
}
