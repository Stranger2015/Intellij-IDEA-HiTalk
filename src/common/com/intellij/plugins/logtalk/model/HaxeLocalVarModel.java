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
import com.intellij.plugins.haxe.model.type.LogtalkTypeResolver;
import com.intellij.plugins.haxe.model.type.ResultHolder;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;

public class LogtalkLocalVarModel extends LogtalkMemberModel {

  private LogtalkLocalVarDeclaration element;

  public LogtalkLocalVarModel(LogtalkLocalVarDeclaration element) {
    super(element);
    this.element = element;
  }

  @Override
  public LogtalkClassModel getDeclaringClass() {
    final LogtalkClass hClass = (LogtalkClass)(element).getContainingClass();
    return hClass != null ? hClass.getModel() : null;
  }
  @Override
  public ResultHolder getResultType() {
    final LogtalkTypeTag typeTag = element.getTypeTag();
    final LogtalkTypeOrAnonymous type = typeTag != null ? getFirstItem(typeTag.getTypeOrAnonymousList()) : null;
    return type != null ? LogtalkTypeResolver.getTypeFromTypeOrAnonymous(type) : null;

  }
  @Override
  public String getPresentableText(LogtalkMethodContext context) {
    final ResultHolder type = getResultType();
    return type == null ? this.getName() : this.getName() + ":" + type;
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return null;
  }

  @Nullable
  @Override
  public FullyQualifiedInfo getQualifiedInfo() {
    return null;
  }

  public boolean hasInitializer() {
    return getInitializerPsi() != null;
  }

  @Nullable
  public LogtalkVarInit getInitializerPsi() {
    final PsiElement basePsi = getBasePsi();
    return basePsi instanceof LogtalkLocalVarDeclaration ? ((LogtalkLocalVarDeclaration)basePsi).getVarInit() : null;
  }

  public boolean hasTypeTag() {
    return getTypeTagPsi() != null;
  }

  public LogtalkTypeTag getTypeTagPsi() {
    final PsiElement basePsi = getBasePsi();
    if (basePsi instanceof LogtalkLocalVarDeclaration) {
      return ((LogtalkLocalVarDeclaration)basePsi).getTypeTag();
    }
    return null;
  }

}
