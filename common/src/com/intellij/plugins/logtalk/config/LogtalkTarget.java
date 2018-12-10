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
package com.intellij.plugins.logtalk.config;

import com.intellij.plugins.logtalk.LogtalkCommonBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


/**
 * @author: Fedor.Korotkov
 */
public enum LogtalkTarget {

  // Target     clFlag    Extension,  OutputDir,  OutputType              Description

  NEKO(         "neko",   ".n",       "neko",     OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.neko")),
  JAVA_SCRIPT(  "js",     ".js",      "js",       OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.js")),
  FLASH(        "swf",    ".swf",     "flash",    OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.swf")),
  CPP(          "cpp",    ".exe",     "cpp",      OUTPUT_TYPE.DIRECTORY,  LogtalkCommonBundle.message("logtalk.target.cpp")),
  CPPIA(        "cppia",  ".cppia",   "cppia",    OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.cppia")),
  PHP(          "php",    ".php",     "php",      OUTPUT_TYPE.DIRECTORY,  LogtalkCommonBundle.message("logtalk.target.php")),
  JAVA(         "java",   ".jar",     "java",     OUTPUT_TYPE.DIRECTORY,  LogtalkCommonBundle.message("logtalk.target.java")),
  CSHARP(       "cs",     ".exe",     "cs",       OUTPUT_TYPE.DIRECTORY,  LogtalkCommonBundle.message("logtalk.target.csharp")),
  PYTHON(       "python", ".py",      "python",   OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.python")),
  LUA(          "lua",    ".lua",     "lua",      OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.lua")),
  PROLOG(       "prolog", ".pl",      "prolog",   OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.tagret.prolog")),
  HILOG(        "hlg",    ".hlg",     "hilog",    OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.hilog")),
  LOGTALK(      "lgt",    ".lgt",     "logtalk",  OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.logtalk")),
  HILOGTALK(    "hilogtalk",".hlgt",  "hilogtalk",OUTPUT_TYPE.FILE,       LogtalkCommonBundle.message("logtalk.target.hilogtalk"));

  private enum OUTPUT_TYPE {
    FILE,
    DIRECTORY,
  }

  private final String flag;
  private final String description;
  private final String outputDir;
  private final String fileExtension;
  private final OUTPUT_TYPE outputType;

  LogtalkTarget(String flag, String fileExtension, String outputDir, OUTPUT_TYPE outputType, String description) {
    this.flag = flag;
    this.description = description;
    this.outputDir = outputDir;
    this.outputType = outputType;
    this.fileExtension = fileExtension;
  }

  public String getFlag() {
    return flag;
  }

  public String getCompilerFlag() {
    return "-" + flag;
  }

  public String getDefaultOutputSubdirectory() {
    return outputDir;
  }

  @NotNull
  public String getTargetFileNameWithExtension(String fileName) {
    return fileName + fileExtension;
  }

  public static void initCombo(@NotNull DefaultComboBoxModel comboBoxModel) {
    for (LogtalkTarget target : LogtalkTarget.values()) {
      comboBoxModel.insertElementAt(target, 0);
    }
  }

  @Override
  public String toString() {
    return description;
  }

  /**
   * Match the string against the compiler's argument/parameter/flag for the
   * target output.
   *
   * @param compilerTargetArgument - string to compare, e.g. '-js', '-neko'
   * @return The target matching the flag, or null, if not found.
   */
  @Nullable
  public static LogtalkTarget matchOutputTarget(String compilerTargetArgument) {
    for (LogtalkTarget t : LogtalkTarget.values()) {
      if (t.getCompilerFlag().equals(compilerTargetArgument)) {
        return t;
      }
    }
    //// as3 is an old case.
    //if ("-as3".equals(compilerTargetArgument)) {
    //  return LogtalkTarget.FLASH;
    //}
    return null;
  }

  public boolean isOutputToDirectory() {
    return outputType == OUTPUT_TYPE.DIRECTORY;
  }
  public boolean isOutputToSingleFile() {
    return outputType == OUTPUT_TYPE.FILE;
  }
}