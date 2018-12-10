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
import com.intellij.openapi.util.Condition;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkClassResolveResult;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponentName;
import com.intellij.plugins.haxe.util.LogtalkMacroUtil;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkArrayVariableMacro extends Macro {
  @Override
  public String getName() {
    return "haxeArrayVariable";
  }

  @Override
  public String getPresentableName() {
    return LogtalkBundle.message("macro.logtalk.array.variable");
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
    final PsiElement at = context.getPsiElementAtStartOffset();
    final Set<LogtalkComponentName> variables = LogtalkMacroUtil.findVariables(at);
    final List<LogtalkComponentName> filtered = ContainerUtil.filter(variables, new Condition<LogtalkComponentName>() {
      @Override
      public boolean value(LogtalkComponentName name) {
        final LogtalkClassResolveResult result = LogtalkResolveUtil.getLogtalkClassResolveResult(name.getParent());
        final LogtalkClass haxeClass = result.getLogtalkClass();
        return haxeClass != null && "Array".equalsIgnoreCase(haxeClass.getQualifiedName());
      }
    });
    return filtered.isEmpty() ? null : new PsiElementResult(filtered.iterator().next());
  }
}
