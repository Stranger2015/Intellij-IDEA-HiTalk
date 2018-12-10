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
package com.intellij.plugins.logtalk.config.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.plugins.haxe.config.sdk.ui.LogtalkAdditionalConfigurablePanel;
import com.intellij.plugins.logtalk.config.sdk.ui.LogtalkAdditionalConfigurablePanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkAdditionalConfigurable implements AdditionalDataConfigurable {
  private final LogtalkAdditionalConfigurablePanel myLogtalkAdditionalConfigurablePanel;
  private Sdk mySdk;

  public LogtalkAdditionalConfigurable() {
    myLogtalkAdditionalConfigurablePanel = new LogtalkAdditionalConfigurablePanel();
  }

  @Override
  public void setSdk(Sdk sdk) {
    mySdk = sdk;
  }

  @Override
  public JComponent createComponent() {
    return myLogtalkAdditionalConfigurablePanel.getPanel();
  }

  @Override
  public boolean isModified() {
    final LogtalkSdkData haxeSdkData = getLogtalkSdkData();
    return haxeSdkData == null ||
           !myLogtalkAdditionalConfigurablePanel.getNekoBinPath().equals(haxeSdkData.getNekoBinPath()) ||
           !myLogtalkAdditionalConfigurablePanel.getLogtalklibPath().equals(haxeSdkData.getLogtalklibPath()) ||
           myLogtalkAdditionalConfigurablePanel.getUseCompilerCompletionFlag() ^ haxeSdkData.getUseCompilerCompletionFlag() ||
           myLogtalkAdditionalConfigurablePanel.getRemoveCompletionDuplicatesFlag() ^ haxeSdkData.getRemoveCompletionDuplicatesFlag();
  }

  @Override
  public void apply() throws ConfigurationException {
    final LogtalkSdkData haxeSdkData = getLogtalkSdkData();
    if (haxeSdkData == null) {
      return;
    }

    final LogtalkSdkData newData = new LogtalkSdkData(haxeSdkData.getHomePath(), haxeSdkData.getVersion());
    newData.setNekoBinPath(FileUtil.toSystemIndependentName(myLogtalkAdditionalConfigurablePanel.getNekoBinPath()));
    newData.setLogtalklibPath(FileUtil.toSystemIndependentName(myLogtalkAdditionalConfigurablePanel.getLogtalklibPath()));
    newData.setUseCompilerCompletionFlag(myLogtalkAdditionalConfigurablePanel.getUseCompilerCompletionFlag());
    newData.setRemoveCompletionDuplicatesFlag(myLogtalkAdditionalConfigurablePanel.getRemoveCompletionDuplicatesFlag());

    final SdkModificator modificator = mySdk.getSdkModificator();
    modificator.setSdkAdditionalData(newData);
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      public void run() {
        modificator.commitChanges();
      }
    });
  }

  @Nullable
  private LogtalkSdkData getLogtalkSdkData() {
    return mySdk.getSdkAdditionalData() instanceof LogtalkSdkData ? (LogtalkSdkData)mySdk.getSdkAdditionalData() : null;
  }

  @Override
  public void reset() {
    final LogtalkSdkData haxeSdkData = getLogtalkSdkData();
    if (haxeSdkData != null) {
      final String nekoBinPath = haxeSdkData.getNekoBinPath();
      myLogtalkAdditionalConfigurablePanel.setNekoBinPath(FileUtil.toSystemDependentName(nekoBinPath == null ? "" : nekoBinPath));
      final String haxelibPath = haxeSdkData.getLogtalklibPath();
      myLogtalkAdditionalConfigurablePanel.setLogtalklibPath(FileUtil.toSystemDependentName(haxelibPath == null ? "" : haxelibPath));
      final boolean bUseCompilerCompletion = haxeSdkData.getUseCompilerCompletionFlag();
      myLogtalkAdditionalConfigurablePanel.setUseCompilerCompletionFlag(bUseCompilerCompletion);
      final boolean bRemoveDuplicates = haxeSdkData.getRemoveCompletionDuplicatesFlag();
      myLogtalkAdditionalConfigurablePanel.setRemoveCompletionDuplicatesFlag(bRemoveDuplicates);
    }
    myLogtalkAdditionalConfigurablePanel.getPanel().repaint();
  }

  @Override
  public void disposeUIResources() {
  }
}
