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
package com.intellij.plugins.logtalk.ide.projectStructure;

import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.ide.projectStructure.ui.LogtalkConfigurationEditor;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.ide.projectStructure.ui.LogtalkConfigurationEditor;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkModuleConfigurationEditor implements ModuleConfigurationEditor {
  private LogtalkConfigurationEditor haxeConfigurationEditor;

  public LogtalkModuleConfigurationEditor(ModuleConfigurationState state) {
    haxeConfigurationEditor = new LogtalkConfigurationEditor(state.getRootModel().getModule(), state.getRootModel().getModuleExtension(
      CompilerModuleExtension.class));
  }

  @Override
  public void saveData() {
  }

  @Override
  public void moduleStateChanged() {
  }

  @Nls
  @Override
  public String getDisplayName() {
    return LogtalkBundle.message("logtalk.module.editor.logtalk");
  }

  @Override
  public String getHelpTopic() {
    return null;
  }

  @Override
  public JComponent createComponent() {
    return haxeConfigurationEditor == null ? null : haxeConfigurationEditor.getMainPanel();
  }

  @Override
  public boolean isModified() {
    return haxeConfigurationEditor != null && haxeConfigurationEditor.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    // FIXME: This is hacky workaround - need to investigate the issue https://github.com/LogtalkFoundation/intellij-haxe/issues/728
    if (haxeConfigurationEditor != null) {
      haxeConfigurationEditor.apply();
      haxeConfigurationEditor = null;
    }
  }

  @Override
  public void reset() {
    if (haxeConfigurationEditor != null) {
      haxeConfigurationEditor.reset();
    }
  }

  @Override
  public void disposeUIResources() {
  }
}
