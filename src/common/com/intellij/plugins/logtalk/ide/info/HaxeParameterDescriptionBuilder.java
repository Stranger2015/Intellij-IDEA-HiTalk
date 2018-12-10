/*
 * Copyright 2017-2017 Ilya Malanin
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
package com.intellij.plugins.logtalk.ide.info;

import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.LogtalkParameterModel;
import com.intellij.plugins.haxe.model.type.LogtalkClassReference;
import com.intellij.plugins.haxe.model.type.LogtalkTypeResolver;
import com.intellij.plugins.haxe.model.type.ResultHolder;
import com.intellij.plugins.haxe.model.type.SpecificLogtalkClassReference;
import com.intellij.plugins.haxe.util.LogtalkPresentableUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LogtalkParameterDescriptionBuilder {

  @NotNull
  static LogtalkParameterDescription[] buildFromList(@Nullable List<LogtalkParameter> parameters, LogtalkClassResolveResult resolveResult) {
    if (parameters == null || parameters.size() == 0) return new LogtalkParameterDescription[0];

    LogtalkParameterDescription[] result = new LogtalkParameterDescription[parameters.size()];

    for (int i = 0; i < parameters.size(); i++) {
      LogtalkParameter parameter = parameters.get(i);
      LogtalkParameterDescription parameterDescription = build(parameter, resolveResult);

      result[i] = parameterDescription;
    }

    return result;
  }

  @NotNull
  private static LogtalkParameterDescription build(LogtalkParameter parameter, LogtalkClassResolveResult resolveResult) {
    final LogtalkParameterModel model = new LogtalkParameterModel(parameter);

    String name = model.getName();
    String type;
    String initialValue = null;

    boolean optional = model.hasOptionalPsi();

    LogtalkTypeTag typeTag = model.getTypeTagPsi();
    LogtalkVarInit varInit = model.getVarInitPsi();

    if (typeTag != null) {
      type = LogtalkPresentableUtil.buildTypeText(parameter, parameter.getTypeTag(), resolveResult.getSpecialization());
    }
    else {
      type = LogtalkPresentableUtil.unknownType();
    }

    if (varInit != null) {
      LogtalkExpression varInitExpression = varInit.getExpression();
      if (varInitExpression != null) {
        initialValue = varInit.getExpression().getText();
      }
    }

    ResultHolder resultHolder = LogtalkTypeResolver.getTypeFromTypeTag(typeTag, parameter);

    String specificTypeText = resultHolder.getType().toStringWithoutConstant();

    if (resolveResult.getSpecialization().containsKey(parameter, specificTypeText)) {
      LogtalkClassResolveResult genericClassResolveResult = resolveResult.getSpecialization().get(parameter, specificTypeText);
      LogtalkClass genericLogtalkClass = genericClassResolveResult.getLogtalkClass();
      PsiElement context = parameter.getContext();
      if (genericLogtalkClass != null && context != null) {
        LogtalkClassReference genericClassReference = new LogtalkClassReference(genericLogtalkClass.getModel(), context);

        resultHolder.setType(SpecificLogtalkClassReference.withoutGenerics(genericClassReference));
      }
    }

    return new LogtalkParameterDescription(name, type, initialValue, optional, resultHolder);
  }
}
