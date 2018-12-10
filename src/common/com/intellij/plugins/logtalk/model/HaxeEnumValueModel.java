/*
 * Copyright 2018 Ilya Malanin
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
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiField;
import com.intellij.plugins.logtalk.util.LogtalkPresentableUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.plugins.haxe.util.LogtalkPresentableUtil.getPresentableParameterList;

public class LogtalkEnumValueModel extends LogtalkMemberModel {
  private final boolean isAbstract;
  private final boolean hasConstructor;
  private final boolean hasReturnType;

  public LogtalkEnumValueModel(@NotNull LogtalkEnumValueDeclaration declaration) {
    super(declaration);

    hasConstructor = declaration.getParameterList() != null;
    hasReturnType = declaration.getReturnType() != null;
    isAbstract = false;
  }

  public LogtalkEnumValueModel(@NotNull LogtalkVarDeclaration declaration) {
    super(declaration);

    isAbstract = true;
    hasConstructor = false;
    hasReturnType = true;
  }

  @Override
  public boolean isStatic() {
    return true;
  }

  @Override
  public boolean isPublic() {
    return !isAbstract() || !hasModifier(LogtalkModifierType.PRIVATE);
  }

  public boolean isAbstract() {
    return this.isAbstract;
  }

  @Override
  public LogtalkClassModel getDeclaringClass() {
    LogtalkClass containingClass = (LogtalkClass)getPsiField().getContainingClass();
    return containingClass != null ? containingClass.getModel() : null;
  }

  @NotNull
  public LogtalkPsiField getPsiField() {
    return (LogtalkPsiField)getBasePsi();
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return getDeclaringClass();
  }

  public boolean hasConstructor() {
    return hasConstructor;
  }

  @Nullable
  public LogtalkParameterList getConstructorParameters() {
    return !hasConstructor ? ((LogtalkEnumValueDeclaration)getBasePsi()).getParameterList() : null;
  }

  @Override
  public String getPresentableText(LogtalkMethodContext context) {
    StringBuilder result = new StringBuilder(getName());
    if (hasConstructor()) {
      result
        .append("(")
        .append(LogtalkPresentableUtil.getPresentableParameterList((LogtalkEnumValueDeclaration)getBasePsi()))
        .append((")"));
    }

    if (hasReturnType) {
      result
        .append(":")
        .append(getResultType().toString());
    }
    return result.toString();
  }
}
