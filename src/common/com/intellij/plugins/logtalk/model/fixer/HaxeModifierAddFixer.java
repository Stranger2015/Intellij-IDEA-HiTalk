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
package com.intellij.plugins.logtalk.model.fixer;

import com.intellij.plugins.haxe.model.LogtalkModifierType;
import com.intellij.plugins.haxe.model.LogtalkModifiersModel;
import com.intellij.plugins.logtalk.model.LogtalkModifierType;
import com.intellij.plugins.logtalk.model.LogtalkModifiersModel;

public class LogtalkModifierAddFixer extends LogtalkFixer {
  private LogtalkModifiersModel modifiers;
  private LogtalkModifierType modifier;

  public LogtalkModifierAddFixer(LogtalkModifiersModel modifiers, LogtalkModifierType modifier) {
    this(modifiers, modifier, "Add " + modifier.s);
  }

  public LogtalkModifierAddFixer(LogtalkModifiersModel modifiers, LogtalkModifierType modifier, String string) {
    super(string);
    this.modifiers = modifiers;
    this.modifier = modifier;
  }

  @Override
  public void run() {
    modifiers.addModifier(modifier);
  }
}
