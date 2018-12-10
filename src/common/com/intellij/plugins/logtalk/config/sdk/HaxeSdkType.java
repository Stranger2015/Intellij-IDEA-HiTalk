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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.logtalk.LogtalkCommonBundle;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;

import javax.swing.*;

public class LogtalkSdkType extends SdkType {
  public LogtalkSdkType() {
    super(LogtalkCommonBundle.message("logtalk.sdk.name"));
  }

  @Override
  public Icon getIcon() {
    return icons.LogtalkIcons.Logtalk_16;
  }

  @Override
  public Icon getIconForAddAction() {
    return icons.LogtalkIcons.Logtalk_16;
  }

  public static LogtalkSdkType getInstance() {
    return SdkType.findInstance(LogtalkSdkType.class);
  }

  @Override
  public String getPresentableName() {
    return LogtalkBundle.message("logtalk.sdk.name.presentable");
  }

  @Override
  public String suggestSdkName(String currentSdkName, String sdkHome) {
    return LogtalkBundle.message("logtalk.sdk.name.suggest", getVersionString(sdkHome));
  }

  @Override
  public String getVersionString(String sdkHome) {
    final LogtalkSdkData haxeSdkData = LogtalkSdkUtil.testLogtalkSdk(sdkHome);
    return haxeSdkData != null ? haxeSdkData.getVersion() : super.getVersionString(sdkHome);
  }

  @Override
  public String suggestHomePath() {
    return LogtalkSdkUtil.suggestHomePath();
  }

  @Override
  public boolean isValidSdkHome(String path) {
    return LogtalkSdkUtil.testLogtalkSdk(path) != null;
  }

  @Override
  public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
    return new LogtalkAdditionalConfigurable();
  }

  @Override
  public boolean isRootTypeApplicable(OrderRootType type) {
    return type == OrderRootType.SOURCES || type == OrderRootType.CLASSES || type == JavadocOrderRootType.getInstance();
  }

  @Override
  public void setupSdkPaths(Sdk sdk) {
    final SdkModificator modificator = sdk.getSdkModificator();

    SdkAdditionalData data = sdk.getSdkAdditionalData();
    if (data == null) {
      data = LogtalkSdkUtil.testLogtalkSdk(sdk.getHomePath());
      modificator.setSdkAdditionalData(data);
    }

    LogtalkSdkUtil.setupSdkPaths(sdk.getHomeDirectory(), modificator);

    modificator.commitChanges();
    super.setupSdkPaths(sdk);
  }

  @Override
  public SdkAdditionalData loadAdditionalData(Element additional) {
    return XmlSerializer.deserialize(additional, LogtalkSdkData.class);
  }

  @Override
  public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
    if (additionalData instanceof LogtalkSdkData) {
      XmlSerializer.serializeInto(additionalData, additional);
    }
  }

  @Override
  public FileChooserDescriptor getHomeChooserDescriptor() {
    final FileChooserDescriptor result = super.getHomeChooserDescriptor();
    if (SystemInfo.isMac) {
      result.withShowHiddenFiles(true); // TODO: Test on a mac: converted for v15.
    }
    return result;
  }
}
