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

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author: Fedor.Korotkov
 */
public enum OpenFLTarget {

  // This mapping of the target to output target is the most likely scenario
  // and is mapped according to the lime templates.  (In other words,
  // this is what target lime will pick for you when you select an
  // OS target.)  This may end up being incorrect, but it's normally
  // correct when degugging and is a lot better than being presented
  // the interface file.
  // Note that the LogtalkTarget is only used for IDEA's convenience and is not
  // passed to the compiler (lime) command, while the flags (third and later
  // arguments) are passed to the compiler.
  IOS("iOS", LogtalkTarget.CPP, "ios", "-simulator"),
  ANDROID("Android", LogtalkTarget.CPP, "android"),
  WEBOS("webOS", LogtalkTarget.CPP, "webos"),
  BLACKBERRY("BlackBerry", LogtalkTarget.CPP, "blackberry"),
  WINDOWS("Windows", LogtalkTarget.CPP, "windows"),
  MAC("Mac OS", LogtalkTarget.CPP, "mac"),
  LINUX("Linux", LogtalkTarget.CPP, "linux"),
  LINUX64("Linux 64", LogtalkTarget.CPP, "linux", "-64"),
  FLASH("Flash", LogtalkTarget.FLASH, "flash"),
  HTML5("HTML5", LogtalkTarget.JAVA_SCRIPT, "html5"),
  NEKO("Neko", LogtalkTarget.NEKO, "neko"),
  TIZEN("Tizen", LogtalkTarget.CPP, "tizen"),
  EMSCRIPTEN("Emscripten", LogtalkTarget.CPP, "emscripten"),
  AIR("Adobe AIR", LogtalkTarget.FLASH, "air");

  private final String[] flags;
  private final String description;
  private final LogtalkTarget outputTarget;

  OpenFLTarget(String description, LogtalkTarget target, String... flags) {
    this.flags = flags;
    this.description = description;
    this.outputTarget = target;
  }

  public String getTargetFlag() {
    return flags.length > 0 ? flags[0] : "";
  }

  public String[] getFlags() {
    return flags;
  }

  public LogtalkTarget getOutputTarget() {
    return outputTarget;
  }

  public static void initCombo(@NotNull DefaultComboBoxModel comboBoxModel) {
    for (OpenFLTarget target : OpenFLTarget.values()) {
      comboBoxModel.insertElementAt(target, 0);
    }
  }

  @Override
  public String toString() {
    return description;
  }
}
