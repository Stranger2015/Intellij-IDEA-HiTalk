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
package com.intellij.plugins.logtalk.ide.template.macro;

import com.intellij.codeInsight.template.*;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponentName;
import com.intellij.plugins.haxe.util.LogtalkMacroUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkSuggestIndexNameMacro extends Macro {
  @Override
  public String getName() {
    return "haxeSuggestIndexName";
  }

  @Override
  public String getPresentableName() {
    return LogtalkBundle.message("macro.logtalk.index.name");
  }

  @NotNull
  @Override
  public String getDefaultValue() {
    return "i";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
    final PsiElement at = context.getPsiElementAtStartOffset();
    final Set<LogtalkComponentName> variables = LogtalkMacroUtil.findVariables(at);
    final Set<String> names = new THashSet<String>(ContainerUtil.map(variables, new Function<LogtalkComponentName, String>() {
      @Override
      public String fun(LogtalkComponentName name) {
        return name.getName();
      }
    }));
    for (char i = 'i'; i < 'z'; ++i) {
      if (!names.contains(Character.toString(i))) {
        return new TextResult(Character.toString(i));
      }
    }
    return null;
  }
}
