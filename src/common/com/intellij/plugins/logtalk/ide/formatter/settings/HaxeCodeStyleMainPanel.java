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
package com.intellij.plugins.logtalk.ide.formatter.settings;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.plugins.haxe.LogtalkLanguage;
import com.intellij.plugins.logtalk.LogtalkLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
  protected LogtalkCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
    super(LogtalkLanguage.INSTANCE, currentSettings, settings);
  }
}
