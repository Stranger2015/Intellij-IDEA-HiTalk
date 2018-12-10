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
package com.intellij.plugins.logtalk.tests.runner.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugins.haxe.ide.module.LogtalkModuleType;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.tests.runner.LogtalkTestsConfiguration;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.ui.ListCellRendererWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class LogtalkTestConfigurationEditorForm extends SettingsEditor<LogtalkTestsConfiguration> {
  private JComboBox myComboModules;
  private JComboBox myComboRunnerClasses;
  private JPanel component;

  public LogtalkTestConfigurationEditorForm(Project project) {
  }

  @Override
  protected void resetEditorFrom(final LogtalkTestsConfiguration configuration) {

    //init modules

    myComboModules.removeAllItems();

    final Module[] modules = ModuleManager.getInstance(configuration.getProject()).getModules();
    for (final Module module : modules) {
      if (ModuleType.get(module) == LogtalkModuleType.getInstance()) {
        myComboModules.addItem(module);
      }
    }
    myComboModules.setSelectedItem(configuration.getConfigurationModule().getModule());
    myComboModules.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateModule((Module)myComboModules.getSelectedItem());
        myComboRunnerClasses.setSelectedIndex(-1);
      }
    });

    //init runner

    myComboRunnerClasses.setSelectedItem(configuration.getRunnerClass());
    myComboRunnerClasses.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LogtalkClass item = (LogtalkClass)myComboRunnerClasses.getSelectedItem();
        if(item != null) {
          configuration.setRunnerClass(item.getName());
        }
      }
    });

    myComboRunnerClasses.setRenderer(new ListCellRendererWrapper() {
      @Override
      public void customize(JList list, Object value, int index, boolean selected, boolean hasFocus) {
        if (value instanceof LogtalkClass) {
          final LogtalkClass haxeClass = (LogtalkClass)value;
          setText(haxeClass.getName());
        }
      }
    });

    //

    updateModule((Module)myComboModules.getSelectedItem());
  }

  private void updateModule(Module module) {
    if (module == null) {
        return;
    }
    myComboRunnerClasses.removeAllItems();

    ModuleRootManager rootManager = ModuleRootManager.getInstance(module);

    List<VirtualFile> roots = rootManager.getSourceRoots(JavaSourceRootType.TEST_SOURCE);
    List<LogtalkClass> classList = new ArrayList<LogtalkClass>();
    for (VirtualFile testSourceRoot : roots) {
      classList.addAll(UsefulPsiTreeUtil.getClassesInDirectory(module.getProject(), testSourceRoot));
    }
    classList.size();
    for (LogtalkClass haxeClass : classList) {
      myComboRunnerClasses.addItem(haxeClass);
    }
  }

  @Override
  protected void applyEditorTo(LogtalkTestsConfiguration configuration) throws ConfigurationException {
    configuration.setModule(getSelectedModule());
  }

  private Module getSelectedModule() {
    return (Module)myComboModules.getSelectedItem();
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return component;
  }
}
