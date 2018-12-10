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
package com.intellij.plugins.logtalk.ide.generation;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkDeclarationAttribute;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.haxe.lang.psi.LogtalkTypeTag;
import com.intellij.plugins.haxe.util.LogtalkPresentableUtil;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;

/**
 * @author: Fedor.Korotkov
 */
public class OverrideImplementMethodFix extends BaseCreateMethodsFix<LogtalkNamedComponent> {
  final boolean override;

  public OverrideImplementMethodFix(final LogtalkClass haxeClass, boolean override) {
    super(haxeClass);
    this.override = override;
  }

  @Override
  protected String buildFunctionsText(LogtalkNamedComponent element) {
    final LogtalkComponentType componentType = LogtalkComponentType.typeOf(element);
    final StringBuilder result = new StringBuilder();

    final PsiClass containingClass = element instanceof PsiMember ? ((PsiMember)element).getContainingClass() : null;
    final boolean isInterfaceElement = containingClass != null && containingClass.isInterface();

    if (!isInterfaceElement && override && !element.isOverride()) {
      result.append("override ");
    }
    final LogtalkDeclarationAttribute[] declarationAttributeList = PsiTreeUtil.getChildrenOfType(element, LogtalkDeclarationAttribute.class);
    if (declarationAttributeList != null) {
      result.append(StringUtil.join(declarationAttributeList, new Function<LogtalkDeclarationAttribute, String>() {
        @Override
        public String fun(LogtalkDeclarationAttribute attribute) {
          return attribute.getText();
        }
      }, " "));
      result.append(" ");
    }
    if (isInterfaceElement && !result.toString().contains("public")) {
      result.insert(0, "public ");
    }
    if (componentType == LogtalkComponentType.FIELD) {
      result.append("var ");
      result.append(element.getName());
    }
    else {
      result.append("function ");
      result.append(element.getName());
      result.append(" (");
      result.append(LogtalkPresentableUtil.getPresentableParameterList(element, specializations));
      result.append(")");
    }
    final LogtalkTypeTag typeTag = PsiTreeUtil.getChildOfType(element, LogtalkTypeTag.class);
    if ((typeTag != null) && (!typeTag.getTypeOrAnonymousList().isEmpty())) {
      result.append(":");
      result.append(LogtalkPresentableUtil.buildTypeText(element, typeTag.getTypeOrAnonymousList().get(0).getType(), specializations));
    }
    result.append(componentType == LogtalkComponentType.FIELD ? ";" : "{\n}");
    return result.toString();
  }
}
