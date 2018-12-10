/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2014 AS3Boyan
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
package org.jetbrains.jps.logtalk.model.sdk.impl;

import com.intellij.plugins.logtalk.config.sdk.LogtalkSdkAdditionalDataBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.logtalk.model.sdk.JpsLogtalkSdkAdditionalData;
import org.jetbrains.jps.model.ex.JpsElementBase;

/**
 * @author: Fedor.Korotkov
 */
public class JpsLogtalkSdkAdditionalDataImpl extends JpsElementBase<JpsLogtalkSdkAdditionalDataImpl> implements JpsLogtalkSdkAdditionalData {
  private LogtalkSdkAdditionalDataBase myAdditionalData;

  public JpsLogtalkSdkAdditionalDataImpl(LogtalkSdkAdditionalDataBase additionalData) {
    myAdditionalData = additionalData;
  }

  @Override
  public LogtalkSdkAdditionalDataBase getSdkData() {
    return myAdditionalData;
  }

  @Override
  public String getHomePath() {
    return myAdditionalData.getHomePath();
  }

  @Override
  public String getVersion() {
    return myAdditionalData.getVersion();
  }

  @Override
  public String getNekoBinPath() {
    return myAdditionalData.getNekoBinPath();
  }

  @Override
  public void setNekoBinPath(String nekoBinPath) {
    myAdditionalData.setNekoBinPath(nekoBinPath);
  }

  @Override
  public String getLogtalklibPath() {
    return myAdditionalData.getLogtalklibPath();
  }

  @Override
  public void setLogtalklibPath(String haxelibPath) {
    myAdditionalData.setLogtalklibPath(haxelibPath);
  }

  @NotNull
  @Override
  public JpsLogtalkSdkAdditionalDataImpl createCopy() {
    return new JpsLogtalkSdkAdditionalDataImpl(myAdditionalData);
  }

  @Override
  public void applyChanges(@NotNull JpsLogtalkSdkAdditionalDataImpl modified) {
    myAdditionalData = modified.myAdditionalData;
  }

  @Override
  public boolean getUseCompilerCompletionFlag() {
    return myAdditionalData.getUseCompilerCompletionFlag();
  }

  @Override
  public void setUseCompilerCompletionFlag(boolean newState) {
    myAdditionalData.setUseCompilerCompletionFlag(newState);
  }

  @Override
  public boolean getRemoveCompletionDuplicatesFlag() {
    return myAdditionalData.getRemoveCompletionDuplicatesFlag();
  }

  @Override
  public void setRemoveCompletionDuplicatesFlag(boolean newState) {
    myAdditionalData.setRemoveCompletionDuplicatesFlag(newState);
  }
}
