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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class LogtalkFunctionDescriptionBuilder {
  @Nullable
  static LogtalkFunctionDescription buildForMethod(LogtalkCallExpression expression) {
    final LogtalkGenericSpecialization specialization = expression.getSpecialization();
    final boolean isStaticExtension = expression.resolveIsStaticExtension();

    final LogtalkReference reference = (LogtalkReference)expression.getExpression();
    final PsiElement target = reference.resolve();

    if (target instanceof LogtalkMethod) {
      final LogtalkClass haxeClass = (LogtalkClass)((LogtalkMethod)target).getContainingClass();
      final LogtalkClassResolveResult resolveResult = LogtalkClassResolveResult.create(haxeClass, specialization);
      return build((LogtalkMethod)target, resolveResult, isStaticExtension);
    }
    return null;
  }

  @Nullable
  static LogtalkFunctionDescription buildForConstructor(final LogtalkNewExpression expression) {
    final LogtalkGenericSpecialization specialization = expression.getSpecialization();
    final LogtalkClass haxeClass = (LogtalkClass)expression.getType().getReferenceExpression().resolve();

    if (haxeClass != null) {
      final PsiMethod[] constructors = haxeClass.getConstructors();
      if (constructors.length > 0) {
        final LogtalkClassResolveResult resolveResult = LogtalkClassResolveResult.create(haxeClass, specialization);

        final LogtalkMethod constructor = (LogtalkMethod)constructors[0];
        return build(constructor, resolveResult, false);
      }
    }

    return null;
  }

  private static LogtalkFunctionDescription build(LogtalkMethod method,
                                               LogtalkClassResolveResult resolveResult,
                                               boolean isExtension) {

    LogtalkParameterDescription[] parameterDescriptions = null;

    final LogtalkParameterList parameterList = PsiTreeUtil.getChildOfType(method, LogtalkParameterList.class);
    if (parameterList != null) {
      List<LogtalkParameter> list = parameterList.getParameterList();
      if (isExtension) {
        list.remove(0);
      }
      parameterDescriptions = LogtalkParameterDescriptionBuilder.buildFromList(list, resolveResult);
    }

    return new LogtalkFunctionDescription(parameterDescriptions);
  }
}
