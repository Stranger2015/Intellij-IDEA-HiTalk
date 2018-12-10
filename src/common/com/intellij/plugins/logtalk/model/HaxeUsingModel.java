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
package com.intellij.plugins.logtalk.model;

import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkReferenceExpression;
import com.intellij.plugins.haxe.lang.psi.LogtalkUsingStatement;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.util.LogtalkResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogtalkUsingModel implements LogtalkModel {
  private final LogtalkUsingStatement basePsi;

  public LogtalkUsingModel(@NotNull LogtalkUsingStatement usingStatement) {
    this.basePsi = usingStatement;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public PsiElement getBasePsi() {
    return basePsi;
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return null;
  }

  public FullyQualifiedInfo getQualifiedInfo() {
    LogtalkReferenceExpression referenceExpression = getReferenceExpression();
    return referenceExpression == null ? null : new FullyQualifiedInfo(referenceExpression);
  }

  public LogtalkReferenceExpression getReferenceExpression() {
    return basePsi.getReferenceExpression();
  }

  @Nullable
  public LogtalkMethodModel findExtensionMethod(String name, @NotNull LogtalkClass classApplyTo) {
    List<LogtalkMethodModel> result = getExtensionMethods(classApplyTo, name);
    return result.isEmpty() ? null : result.get(0);
  }

  @NotNull
  public List<LogtalkMethodModel> getExtensionMethods(@NotNull LogtalkClass classApplyTo) {
    return getExtensionMethods(classApplyTo, null);
  }

  @NotNull
  private List<LogtalkMethodModel> getExtensionMethods(LogtalkClass classApplyTo, @Nullable String name) {
    List<LogtalkClassModel> classes = getClassModels();
    if (classes == null || classes.isEmpty()) return Collections.emptyList();

    List<LogtalkMethodModel> result = null;

    for (LogtalkClassModel classModel : classes) {
      List<LogtalkMethodModel> methods = null;
      if (name != null) {
        LogtalkMethodModel method = classModel.getMethod(name);
        if (method != null) methods = Collections.singletonList(method);
      }
      else {
        methods = classModel.getMethods();
      }

      if (methods == null || methods.isEmpty()) continue;

      for (LogtalkMethodModel method : methods) {
        if (method != null && !method.isConstructor() && method.isStatic() && method.isPublic()) {
          List<LogtalkParameterModel> parameters = method.getParameters();
          if (!parameters.isEmpty()) {
            //FIXME Switch to specific type with generics
            final LogtalkClass firstParameterType = LogtalkResolveUtil.getLogtalkClassResolveResult(parameters.get(0).getBasePsi()).getLogtalkClass();
            final HashSet<LogtalkClass> baseClassesSet = LogtalkResolveUtil.getBaseClassesSet(classApplyTo);

            final boolean applicable = firstParameterType == classApplyTo || baseClassesSet.contains(firstParameterType);

            if (applicable) {
              if (result == null) result = new ArrayList<>();
              result.add(method);
            }
          }
        }
      }
    }

    return result == null ? Collections.emptyList() : result;
  }

  public List<LogtalkClassModel> getClassModels() {
    List<LogtalkModel> result = LogtalkProjectModel.fromElement(this.basePsi).resolve(getQualifiedInfo(), this.basePsi.getResolveScope());
    if (result == null || result.isEmpty()) return null;

    return result.stream()
      .flatMap(model -> (model instanceof LogtalkFileModel)
                        ? ((LogtalkFileModel)model).getClassModels().stream()
                        : Stream.of(model))
      .filter(model -> model instanceof LogtalkClassModel)
      .map(model -> (LogtalkClassModel)model)
      .collect(Collectors.toList());
  }
}
