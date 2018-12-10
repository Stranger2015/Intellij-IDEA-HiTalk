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

import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkFunctionPrototypeDeclarationWithAttributes;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkImplementMethodHandler extends BaseLogtalkGenerateHandler {
  @Override
  protected String getTitle() {
    return LogtalkBundle.message("logtalk.implement.method");
  }

  @Override
  void collectCandidates(LogtalkClass haxeClass, List<LogtalkNamedComponent> candidates) {
    for (LogtalkNamedComponent haxeNamedComponent : LogtalkResolveUtil.findNamedSubComponents(haxeClass)) {
      final boolean prototype = haxeNamedComponent instanceof LogtalkFunctionPrototypeDeclarationWithAttributes;
      final LogtalkClass parentClass = PsiTreeUtil.getParentOfType(haxeNamedComponent, LogtalkClass.class, true);
      final boolean interfaceField = LogtalkComponentType.typeOf(haxeNamedComponent) == LogtalkComponentType.FIELD &&
                                     LogtalkComponentType.typeOf(parentClass) == LogtalkComponentType.INTERFACE;
      if (!prototype && !interfaceField) continue;

      candidates.add(haxeNamedComponent);
    }
  }

  @Override
  protected BaseCreateMethodsFix createFix(LogtalkClass haxeClass) {
    return new OverrideImplementMethodFix(haxeClass, false);
  }
}
