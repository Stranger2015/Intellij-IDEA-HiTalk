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
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiField;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LogtalkFieldModel extends LogtalkMemberModel {

  public LogtalkFieldModel(@NotNull LogtalkPsiField element) {
    super(element);
  }

  private LogtalkClassModel _declaringClass = null;

  public LogtalkClassModel getDeclaringClass() {
    if (_declaringClass == null) {
      LogtalkClass aClass = (LogtalkClass)getPsiField().getContainingClass();
      _declaringClass = (aClass != null) ? aClass.getModel() : null;
    }
    return _declaringClass;
  }

  @Nullable
  public LogtalkPropertyDeclaration getPropertyDeclarationPsi() {
    final PsiElement basePsi = getBasePsi();
    return basePsi instanceof LogtalkVarDeclaration ? ((LogtalkVarDeclaration)basePsi).getPropertyDeclaration() : null;
  }

  @Nullable
  public LogtalkPropertyAccessor getAccessorPsi(int index) {
    if (getPropertyDeclarationPsi() == null) return null;
    List<LogtalkPropertyAccessor> list = getPropertyDeclarationPsi().getPropertyAccessorList();
    return (list.size() >= index) ? list.get(index) : null;
  }

  @NotNull
  public LogtalkPsiField getPsiField() {
    return (LogtalkPsiField)getBasePsi();
  }

  @Nullable
  public LogtalkPropertyAccessor getGetterPsi() {
    return getAccessorPsi(0);
  }

  @Nullable
  public LogtalkPropertyAccessor getSetterPsi() {
    return getAccessorPsi(1);
  }

  public LogtalkAccessorType getSetterType() {
    return LogtalkAccessorType.fromPsi(getSetterPsi());
  }

  public LogtalkAccessorType getGetterType() {
    return LogtalkAccessorType.fromPsi(getGetterPsi());
  }

  public boolean isProperty() {
    return getPropertyDeclarationPsi() != null;
  }

  public boolean isReadableFromOutside() {
    return isPublic() && (isRealVar() || this.getGetterType().isAllowedFromOutside());
  }

  public boolean isReadableFromInside() {
    return isRealVar() || this.getGetterType().isAllowedFromInside();
  }

  public boolean isWritableFromOutside() {
    return isPublic() && (isRealVar() || this.getSetterType().isAllowedFromOutside());
  }

  public boolean isWritableFromInside() {
    return isRealVar() || this.getSetterType().isAllowedFromInside();
  }

  public LogtalkMethodModel getGetterMethod() {
    if (getGetterType() != LogtalkAccessorType.GET) return null;
    return this.getDeclaringClass().getMethod("get_" + this.getName());
  }

  public LogtalkMethodModel getSetterMethod() {
    if (getSetterType() != LogtalkAccessorType.SET) return null;
    return this.getDeclaringClass().getMethod("set_" + this.getName());
  }

  public boolean isRealVar() {
    if (this.getModifiers().hasModifier(LogtalkModifierType.IS_VAR)) return true;
    if (!isProperty()) return true;
    LogtalkAccessorType setter = getSetterType();
    LogtalkAccessorType getter = getGetterType();
    if (setter == LogtalkAccessorType.NULL || setter == LogtalkAccessorType.DEFAULT) {
      return true;
    } else if (setter == LogtalkAccessorType.NEVER &&
               (getter == LogtalkAccessorType.DEFAULT || getter == LogtalkAccessorType.NULL)) {
      return true;
    }
    return false;
  }

  public boolean hasInitializer() {
    return getInitializerPsi() != null;
  }

  @Nullable
  public LogtalkVarInit getInitializerPsi() {
    final PsiElement basePsi = getBasePsi();
    return basePsi instanceof LogtalkVarDeclaration ? ((LogtalkVarDeclaration)basePsi).getVarInit() : null;
  }

  public boolean hasTypeTag() {
    return getTypeTagPsi() != null;
  }

  public LogtalkTypeTag getTypeTagPsi() {
    final PsiElement basePsi = getBasePsi();
    if (basePsi instanceof LogtalkAnonymousTypeField) {
      return ((LogtalkAnonymousTypeField)basePsi).getTypeTag();
    }
    if (basePsi instanceof LogtalkVarDeclaration) {
      return ((LogtalkVarDeclaration)basePsi).getTypeTag();
    }

    return null;
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return getDeclaringClass();
  }
}
