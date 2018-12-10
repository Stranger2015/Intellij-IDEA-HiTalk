/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2014 AS3Boyan
 * Copyright 2014-2014 Elias Ku
 * Copyright 2017-2018 Eric Bishton
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
package com.intellij.plugins.logtalk.ide.module;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.plugins.logtalk.config.LogtalkConfiguration;
import com.intellij.plugins.logtalk.config.LogtalkTarget;
import com.intellij.plugins.logtalk.config.NMETarget;
import com.intellij.plugins.logtalk.config.OpenFLTarget;
import com.intellij.plugins.logtalk.module.LogtalkModuleSettingsBase;
import com.intellij.plugins.logtalk.module.impl.LogtalkModuleSettingsBaseImpl;
import com.intellij.plugins.logtalk.util.LogtalkModificationTracker;
import com.intellij.plugins.logtalk.util.LogtalkTrackedModifiable;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author: Fedor.Korotkov
 */
@State(
  name = "LogtalkModuleSettingsStorage",
  storages = {
    @Storage(
      file = "$MODULE_FILE$"
    )
  }
)
public class LogtalkModuleSettings extends LogtalkModuleSettingsBaseImpl
  implements PersistentStateComponent<LogtalkModuleSettings>, LogtalkModuleSettingsBase, LogtalkTrackedModifiable {

  private static Logger LOG = Logger.getInstance("#" + LogtalkModuleSettings.class.getName());

  private String flexSdkName = "";
  private LogtalkModificationTracker tracker = new LogtalkModificationTracker(this.getClass().getName());

  public LogtalkModuleSettings() {
  }

  public LogtalkModuleSettings(String mainClass,
                            LogtalkTarget logtalkTarget,
                            NMETarget nmeTarget,
                            OpenFLTarget openFLTarget,
                            String arguments,
                            String nmeFlags,
                            boolean excludeFromCompilation,
                            boolean keepSynchronizedWithProjectFile,
                            String outputFileName,
                            String outputFolder,
                            String flexSdkName,
                            int buildConfig,
                            String hxmlPath,
                            String nmmlPath,
                            String openFLPath) {
    super(mainClass, outputFileName, outputFolder, arguments, nmeFlags, excludeFromCompilation, keepSynchronizedWithProjectFile, logtalkTarget, nmeTarget, openFLTarget, hxmlPath, nmmlPath,
          openFLPath, buildConfig);
    this.flexSdkName = flexSdkName;
    notifyUpdated();
  }

  @Override
  public LogtalkModuleSettings getState() {
    return this;
  }

  @Override
  public void loadState(LogtalkModuleSettings state) {
    XmlSerializerUtil.copyBean(state, this);
    notifyUpdated(); // Not technically necessary, because the setters also trigger a notification.
  }

  public void setFlexSdkName(String flexSdkName) {
    this.flexSdkName = flexSdkName;
    notifyUpdated();
  }

  public String getFlexSdkName() {
    return flexSdkName;
  }

  public LogtalkTarget getCompilationTarget() {
    LogtalkTarget defaultTarget;
    String targetArgs;

    if (isUseNmmlToBuild()) {             // NME
      defaultTarget = getNmeTarget().getOutputTarget();
      targetArgs = getNmeFlags();
    } else if (isUseOpenFLToBuild()) {    // OpenFL
      defaultTarget = getOpenFLTarget().getOutputTarget();
      targetArgs = getOpenFLFlags();
    } else {                              // HXML or logtalk compiler
      defaultTarget = getLogtalkTarget();
      targetArgs = getArguments();
    }

    LogtalkTarget t = getTargetFromCompilerArguments(targetArgs);
    if (null == t) {
      t = defaultTarget;
    }
    return t;
  }

  @Nullable
  private static LogtalkTarget getTargetFromCompilerArguments(String arguments) {
    LogtalkTarget target = null;
    if (null != arguments && !arguments.isEmpty()) {
      String[] args = arguments.split(" ");
      for (String a : args) {
        LogtalkTarget matched = LogtalkTarget.matchOutputTarget(a);
        if (null != matched) {
          target = matched;
          break;
        }
      }
    }
    return target;
  }


  public static LogtalkModuleSettings getInstance(@NotNull Module module) {
    return ModuleServiceManager.getService(module, LogtalkModuleSettings.class);
  }

  @Override
  protected void notifyUpdated() {
    tracker.notifyUpdated();
  }

  @Override
  public Stamp getStamp() {
    return tracker.getStamp();
  }

  @Override
  public boolean isModifiedSince(Stamp s) {
    return tracker.isModifiedSince(s);
  }

  @Override
  public boolean equals(Object o) {
    // Modification tracker is NOT part of equals or hashCode.

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LogtalkModuleSettings settings = (LogtalkModuleSettings)o;

    if (excludeFromCompilation != settings.excludeFromCompilation) return false;
    if (keepSynchronizedWithProjectFile != settings.keepSynchronizedWithProjectFile) return false;
    if (buildConfig != settings.buildConfig) return false;
    if (arguments != null ? !arguments.equals(settings.arguments) : settings.arguments != null) return false;
    if (nmeFlags != null ? !nmeFlags.equals(settings.nmeFlags) : settings.nmeFlags != null) return false;
    if (openFLFlags != null ? !openFLFlags.equals(settings.openFLFlags) : settings.openFLFlags != null) return false;
    if (flexSdkName != null ? !flexSdkName.equals(settings.flexSdkName) : settings.flexSdkName != null) return false;
    if (hxmlPath != null ? !hxmlPath.equals(settings.hxmlPath) : settings.hxmlPath != null) return false;
    if (mainClass != null ? !mainClass.equals(settings.mainClass) : settings.mainClass != null) return false;
    if (outputFileName != null ? !outputFileName.equals(settings.outputFileName) : settings.outputFileName != null) return false;
    if (outputFolder!= null ? !outputFolder.equals(settings.outputFolder) : settings.outputFolder!= null) return false;
    if (logtalkTarget != settings.logtalkTarget) return false;
    if (nmeTarget != settings.nmeTarget) return false;
    if (openFLTarget != settings.openFLTarget) return false;

    return true;
  }

  @Override
  public int hashCode() {
    // Modification tracker is NOT part of equals or hashCode.

    int result = mainClass != null ? mainClass.hashCode() : 0;
    result = 31 * result + (outputFileName != null ? outputFileName.hashCode() : 0);
    result = 31 * result + (outputFolder != null ? outputFolder.hashCode() : 0);
    result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
    result = 31 * result + (nmeFlags != null ? nmeFlags.hashCode() : 0);
    result = 31 * result + (openFLFlags != null ? openFLFlags.hashCode() : 0);
    result = 31 * result + (excludeFromCompilation ? 1 : 0);
    result = 31 * result + (keepSynchronizedWithProjectFile ? 1 : 0);
    result = 31 * result + (logtalkTarget != null ? logtalkTarget.hashCode() : 0);
    result = 31 * result + (nmeTarget != null ? nmeTarget.hashCode() : 0);
    result = 31 * result + (openFLTarget != null ? openFLTarget.hashCode() : 0);
    result = 31 * result + (flexSdkName != null ? flexSdkName.hashCode() : 0);
    result = 31 * result + (hxmlPath != null ? hxmlPath.hashCode() : 0);
    result = 31 * result + buildConfig;
    return result;
  }



  /**
   * Convenience method to find the project file.
   */
  @Nullable
  public String getLogtalkProjectPath() {
    switch(LogtalkConfiguration.translateBuildConfig(buildConfig)) {
      case HXML:        return getHxmlPath();
      case OPENFL:      return getOpenFLPath();
      case NMML:        return getNmmlPath();
      case CUSTOM:      return findProjectFileName(getArguments());
      default:
        LOG.warn("Internal error: Unknown buildConfig (build type) in project settings.");
        return null;
    }
  }

  @Nullable
  private static String findProjectFileName(String args) {

    // TODO: Parse the project arguments using an HXML project model.  Can't do it until HXML project model can accept multiple args on one line.
    // So, for the moment, we're just going to cheat and look for something that looks like a project file name.

    for (String arg : args.split(" ")) {
      if (!arg.startsWith("-")) {
        // We don't have enough info to verify that a file exists (we don't have the module's root path).
        // So, if the file name matches a known extension, then pass that back.
        int extpos = arg.lastIndexOf('.');
        if (-1 != extpos) {
          String ext = arg.substring(extpos + 1);
          if ("nmml".equals(ext) || "xml".equals(ext) || "hxml".equals(ext)) {
            return arg;
          }
        }
      }
    }
    return null;
  }



}
