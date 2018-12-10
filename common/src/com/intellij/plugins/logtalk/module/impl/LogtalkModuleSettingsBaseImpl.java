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
package com.intellij.plugins.logtalk.module.impl;

import com.intellij.plugins.logtalk.config.LogtalkTarget;
import com.intellij.plugins.logtalk.config.NMETarget;
import com.intellij.plugins.logtalk.module.LogtalkModuleSettingsBase;
import com.intellij.plugins.logtalk.config.LogtalkConfiguration;
import com.intellij.plugins.logtalk.config.OpenFLTarget;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkModuleSettingsBaseImpl implements LogtalkModuleSettingsBase {
  protected String mainClass = "";
  protected String outputFileName = "";
  protected String outputFolder = "";
  protected String arguments = "";
  protected String nmeFlags = "";
  protected String openFLFlags = "";
  protected boolean excludeFromCompilation = false;
  protected boolean keepSynchronizedWithProjectFile = true;
  protected LogtalkTarget haxeTarget = LogtalkTarget.NEKO;
  protected NMETarget nmeTarget = NMETarget.FLASH;
  protected OpenFLTarget openFLTarget = OpenFLTarget.FLASH;
  protected String hxmlPath = "";
  protected String nmmlPath = "";
  protected String openFLPath = "";
  protected int buildConfig = 0;


  public LogtalkModuleSettingsBaseImpl() {
  }

  public LogtalkModuleSettingsBaseImpl(String mainClass,
                                    String outputFileName,
                                    String outputFolder,
                                    String arguments,
                                    String nmeFlags,
                                    boolean excludeFromCompilation,
                                    boolean keepSynchronizedWithProjectFile,
                                    LogtalkTarget haxeTarget,
                                    NMETarget nmeTarget,
                                    OpenFLTarget openFLTarget,
                                    String hxmlPath,
                                    String nmmlPath,
                                    String openFLPath,
                                    int buildConfig) {
    this.mainClass = mainClass;
    this.outputFileName = outputFileName;
    this.outputFolder = outputFolder;
    this.arguments = arguments;
    this.nmeFlags = nmeFlags;
    this.excludeFromCompilation = excludeFromCompilation;
    this.keepSynchronizedWithProjectFile = keepSynchronizedWithProjectFile;
    this.haxeTarget = haxeTarget;
    this.nmeTarget = nmeTarget;
    this.openFLTarget = openFLTarget;
    this.hxmlPath = hxmlPath;
    this.nmmlPath = nmmlPath;
    this.openFLPath = openFLPath;
    this.buildConfig = buildConfig;
  }

  public void setNmeTarget(NMETarget nmeTarget) {
    this.nmeTarget = nmeTarget;
    notifyUpdated();
  }

  public int getBuildConfig() {
    return buildConfig;
  }

  public LogtalkConfiguration getBuildConfiguration() {
    return LogtalkConfiguration.translateBuildConfig(getBuildConfig());
  }

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
    notifyUpdated();
  }

  public String getArguments() {
    return arguments;
  }

  public void setArguments(String arguments) {
    this.arguments = arguments;
    notifyUpdated();
  }

  public String getNmeFlags() {
    return nmeFlags;
  }

  public void setNmeFlags(String flags) {
    this.nmeFlags = flags;
    notifyUpdated();
  }

  public void setOpenFLFlags(String flags) {
    this.openFLFlags = flags;
    notifyUpdated();
  }

  public String getOpenFLFlags() {
    return openFLFlags;
  }

  public void setOpenFLPath(String openFLPath) { this.openFLPath = openFLPath; notifyUpdated(); }

  public String getOpenFLPath() {
    return openFLPath;
  }

  public void setOpenFLTarget(OpenFLTarget target) {
    this.openFLTarget = target;
    notifyUpdated();
  }

  public OpenFLTarget getOpenFLTarget() {
    return openFLTarget;
  }

  public LogtalkTarget getLogtalkTarget() {
    return haxeTarget;
  }

  public NMETarget getNmeTarget() {
    return nmeTarget;
  }

  public void setLogtalkTarget(LogtalkTarget haxeTarget) {
    this.haxeTarget = haxeTarget;
    notifyUpdated();
  }

  public boolean isExcludeFromCompilation() {
    return excludeFromCompilation;
  }

  public void setExcludeFromCompilation(boolean excludeFromCompilation) {
    this.excludeFromCompilation = excludeFromCompilation;
    notifyUpdated();
  }

  public boolean isKeepSynchronizedWithProjectFile() {
    return keepSynchronizedWithProjectFile;
  }

  public void setKeepSynchronizedWithProjectFile(boolean keepSynchronizedWithProjectFile) {
    this.keepSynchronizedWithProjectFile = keepSynchronizedWithProjectFile;
    notifyUpdated();
  }

  public String getOutputFileName() {
    return outputFileName;
  }

  public void setOutputFileName(String outputFileName) {
    this.outputFileName = outputFileName;
    notifyUpdated();
  }

  public String getOutputFolder() { return outputFolder; }

  public void setOutputFolder(String outputFolder) { this.outputFolder = outputFolder; notifyUpdated(); }

  public String getHxmlPath() {
    return hxmlPath;
  }

  public String getNmmlPath() {
    return nmmlPath;
  }

  public void setHxmlPath(String hxmlPath) {
    this.hxmlPath = hxmlPath;
    notifyUpdated();
  }

  public boolean isUseHxmlToBuild() {
    return LogtalkConfiguration.translateBuildConfig(buildConfig) == LogtalkConfiguration.HXML;
  }

  public boolean isUseNmmlToBuild() {
    return LogtalkConfiguration.translateBuildConfig(buildConfig) == LogtalkConfiguration.NMML;
  }

  public boolean isUseOpenFLToBuild() {
    return LogtalkConfiguration.translateBuildConfig(buildConfig) == LogtalkConfiguration.OPENFL;
  }

  public boolean isUseUserPropertiesToBuild() {
    return LogtalkConfiguration.translateBuildConfig(buildConfig) == LogtalkConfiguration.CUSTOM;
  }

  public void setNmmlPath(String nmmlPath) {
    this.nmmlPath = nmmlPath;
    notifyUpdated();
  }

  public void setBuildConfig(int buildConfig) {
    this.buildConfig = buildConfig;
    notifyUpdated();
  }

  protected void notifyUpdated() { return; }

}
