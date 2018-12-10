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

import com.intellij.plugins.haxe.lang.psi.LogtalkAbstractBody;
import com.intellij.plugins.haxe.lang.psi.LogtalkAbstractClassDeclaration;
import com.intellij.plugins.haxe.lang.psi.LogtalkVarDeclaration;
import com.intellij.plugins.haxe.util.LogtalkAbstractEnumUtil;
import com.intellij.plugins.logtalk.util.LogtalkAbstractEnumUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogtalkAbstractEnumModel extends LogtalkAbstractClassModel implements LogtalkEnumModel {
  public LogtalkAbstractEnumModel(@NotNull LogtalkAbstractClassDeclaration haxeClass) {
    super(haxeClass);
  }

  @Override
  public LogtalkEnumValueModel getValue(@NotNull String name) {
    LogtalkVarDeclaration value = getValueDeclarationsStream()
      .filter(declaration -> name.equals(declaration.getName()))
      .filter(LogtalkAbstractEnumUtil::couldBeAbstractEnumField)
      .findFirst().orElse(null);

    return value != null ? (LogtalkEnumValueModel)value.getModel() : null;
  }

  @Override
  public List<LogtalkEnumValueModel> getValues() {
    return getValuesStream().collect(Collectors.toList());
  }

  @Override
  public Stream<LogtalkEnumValueModel> getValuesStream() {
    return getValueDeclarationsStream()
      .filter(LogtalkAbstractEnumUtil::couldBeAbstractEnumField)
      .map(model -> (LogtalkEnumValueModel)model.getModel());
  }

  private Stream<LogtalkVarDeclaration> getValueDeclarationsStream() {
    final LogtalkAbstractBody body = getAbstractClassBody();

    return body != null ? body.getVarDeclarationList().stream() : Stream.empty();
  }
}
