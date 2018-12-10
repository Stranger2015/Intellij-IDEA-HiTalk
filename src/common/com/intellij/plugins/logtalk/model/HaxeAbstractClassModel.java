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
import com.intellij.plugins.logtalk.lang.psi.LogtalkClassResolveResult;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiCompositeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LogtalkAbstractClassModel extends LogtalkClassModel {
  public LogtalkAbstractClassModel(@NotNull LogtalkAbstractClassDeclaration haxeClass) {
    super(haxeClass);
  }

  @Nullable
  @Override
  public LogtalkPsiCompositeElement getBodyPsi() {
    return getAbstractClassBody();
  }

  public boolean hasForwards() {
    return hasMeta("@:forward");
  }

  public LogtalkClass getUnderlyingClass() {
    LogtalkUnderlyingType underlyingTypePsi = getAbstractClass().getUnderlyingType();
    if (underlyingTypePsi == null) return null;
    final LogtalkType underlyingType = underlyingTypePsi.getTypeOrAnonymousList().get(0).getType();
    if (underlyingType != null) {
      final LogtalkClassResolveResult result = underlyingType.getReferenceExpression().resolveLogtalkClass();
      return result.getLogtalkClass();
    }

    return null;
  }

  public LogtalkAbstractClassDeclaration getAbstractClass() {
    return (LogtalkAbstractClassDeclaration)getBasePsi();
  }

  protected LogtalkAbstractBody getAbstractClassBody() {
    return getAbstractClass().getAbstractBody();
  }
}
