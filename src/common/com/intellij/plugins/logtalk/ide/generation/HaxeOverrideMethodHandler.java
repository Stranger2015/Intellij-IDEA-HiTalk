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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkFunctionDeclarationWithAttributes;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.haxe.model.LogtalkClassModel;
import com.intellij.plugins.haxe.model.LogtalkMethodModel;
import com.intellij.plugins.haxe.model.LogtalkModifierType;
import com.intellij.plugins.haxe.model.LogtalkModifiersModel;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.log4j.Level;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkOverrideMethodHandler extends BaseLogtalkGenerateHandler {

  private static final Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.ide.generation.LogtalkOverrideMethodHandler");
  {
    LOG.info("Loaded LogtalkOverrideMethodHandler");
    LOG.setLevel(Level.DEBUG);
  }

  @Override
  protected String getTitle() {
    return LogtalkBundle.message("logtalk.override.method");
  }

  @Override
  void collectCandidates(LogtalkClass haxeClass, List<LogtalkNamedComponent> candidates) {
    LogtalkClassModel clazz = haxeClass.getModel();

    for (LogtalkMethodModel method : clazz.getAncestorMethods()) {
      // Only add methods that doesn't have @:final or static modifiers and also that are not constructors
      if (
        !method.getModifiers().hasAnyModifier(
          LogtalkModifierType.FINAL,
          LogtalkModifierType.STATIC
        ) &&
        !method.isConstructor()
      ) {
        candidates.add(method.getMethodPsi());
      }
    }
  }

  @Override
  protected BaseCreateMethodsFix createFix(LogtalkClass haxeClass) {
    return new OverrideImplementMethodFix(haxeClass, true);
  }
}
