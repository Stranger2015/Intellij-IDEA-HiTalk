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

import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.type.LogtalkGenericResolver;
import com.intellij.plugins.haxe.model.type.LogtalkTypeResolver;
import com.intellij.plugins.haxe.model.type.ResultHolder;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkMethod;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiField;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;

public class LogtalkParameterModel extends LogtalkMemberModel {

  final private LogtalkParameter parameter;
  final private LogtalkMemberModel memberModel;
  final private boolean optional;

  public LogtalkParameterModel(LogtalkParameter parameter) {
    super(parameter);

    this.parameter = parameter;
    this.optional = UsefulPsiTreeUtil.getToken(parameter, "?") != null;

    final PsiMember parentPsi = PsiTreeUtil.getParentOfType(parameter, LogtalkEnumValueDeclaration.class, LogtalkMethod.class);
    if (parentPsi instanceof LogtalkMethod) {
      memberModel = ((LogtalkMethod)parentPsi).getModel();
    }
    else if (parentPsi instanceof LogtalkEnumValueDeclaration) {
      memberModel = new LogtalkFieldModel((LogtalkPsiField)parentPsi);
    }
    else {
      memberModel = null;
    }
  }

  public PsiElement getContextElement() {
    return getNameOrBasePsi();
  }

  public PsiElement getOptionalPsi() {
    return UsefulPsiTreeUtil.getToken(parameter, "?");
  }

  public boolean hasOptionalPsi() {
    return this.optional;
  }

  public boolean isOptional() {
    return this.hasOptionalPsi() || this.hasInit();
  }

  public boolean hasInit() {
    return getVarInitPsi() != null;
  }

  public LogtalkVarInit getVarInitPsi() {
    return UsefulPsiTreeUtil.getChild(parameter, LogtalkVarInit.class);
  }

  public LogtalkTypeTag getTypeTagPsi() {
    return parameter.getTypeTag();
  }

  public ResultHolder getType() {
    return getType(null);
  }

  public ResultHolder getType(@Nullable LogtalkGenericResolver resolver) {
    if (resolver != null) {
      ResultHolder typeResult = getType(null);
      ResultHolder resolved = resolver.resolve(typeResult.getType().toStringWithoutConstant());
      if (resolved != null) return resolved;
    }
    return LogtalkTypeResolver.getTypeFromTypeTag(getTypeTagPsi(), this.getContextElement());
  }

  public PsiParameter getParameter() {
    return parameter;
  }

  public LogtalkMemberModel getMemberModel() {
    return memberModel;
  }

  public String getPresentableText() {
    String out = hasOptionalPsi() ? "?" : "";
    out += getName();
    out += ":";
    out += getType().toStringWithoutConstant();
    return out;
  }

  @Override
  public LogtalkComponentName getNamePsi() {
    return parameter.getComponentName();
  }

  @Override
  public LogtalkDocumentModel getDocument() {
    return memberModel.getDocument();
  }

  @Override
  public LogtalkClassModel getDeclaringClass() {
    return memberModel.getDeclaringClass();
  }

  @Override
  public ResultHolder getResultType() {
    final LogtalkTypeTag typeTag = parameter.getTypeTag();
    final LogtalkTypeOrAnonymous type = typeTag != null ? getFirstItem(typeTag.getTypeOrAnonymousList()) : null;
    return type != null ? LogtalkTypeResolver.getTypeFromTypeOrAnonymous(type) : null;
  }

  @Override
  public String getPresentableText(LogtalkMethodContext context) {
    final ResultHolder type = getResultType();
    return type == null ? this.getName() : this.getName() + ":" + type;
  }

  public void remove() {
    PsiElement psi = getBasePsi();
    if (psi != null) {
      PsiElement prePsi = UsefulPsiTreeUtil.getPrevSiblingSkipWhiteSpaces(psi, true);
      PsiElement nextPsi = UsefulPsiTreeUtil.getNextSiblingNoSpaces(psi);
      TextRange range = psi.getTextRange();
      StripSpaces stripSpaces = StripSpaces.NONE;

      if (prePsi != null && prePsi.getText().equals(",")) {
        range = range.union(prePsi.getTextRange());
        stripSpaces = StripSpaces.BEFORE;
      }
      else if (nextPsi != null && nextPsi.getText().equals(",")) {
        range = range.union(nextPsi.getTextRange());
        stripSpaces = StripSpaces.AFTER;
      }
      getDocument().replaceElementText(range, "", stripSpaces);
    }
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return getMemberModel() instanceof LogtalkExposableModel ? (LogtalkExposableModel)getMemberModel() : null;
  }

  @Nullable
  @Override
  public FullyQualifiedInfo getQualifiedInfo() {
    return null;
  }
}
