/*
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
package com.intellij.plugins.logtalk.config;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.InvalidDataException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Enumerate the types of Logtalk project configurations that are available.
 *
 */
public enum LogtalkConfiguration {

  CUSTOM("Custom Configuration", null, ConfigValues.USE_PROPERTIES), // TODO:Internationalize??
  HXML("HXML", ".hxml", ConfigValues.USE_HXML),
  NMML("NMML", ".nmml", ConfigValues.USE_NMML),
  OPENFL("OpenFL", ".xml", ConfigValues.USE_OPENFL);

  private class ConfigValues {
    static final int USE_PROPERTIES = 0;
    static final int USE_HXML = 1;
    static final int USE_NMML = 2;
    static final int USE_OPENFL = 3;
  }

  private static final Logger LOG = Logger.getInstance("#LogtalkConfiguration");
  private final String myName;
  private final String myExtension;
  private final int myBuildConfig; // Temporary until LogtalkModuleSettins.USE_XXX are converted.

  LogtalkConfiguration(@NotNull String displayName, String extension, int buildConfig) {
    myName = displayName;
    myExtension = extension;
    myBuildConfig = buildConfig;
  }

  public int asBuildConfigValue() {
    return myBuildConfig;
  }

  /**
   * @return The configuration to use when none has been previously selected.
   */
  @NotNull
  public static LogtalkConfiguration getDefault() {
    // XXX: Is there a more appropriate place for this??  LogtalkProjectModel, maybe?
    return CUSTOM;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @Nullable
  public String getProjectFileExtension() {
    return myExtension;
  }

  @Override
  public String toString() {
    return myName;
  }

  public static LogtalkConfiguration translateBuildConfig(int buildConfig) {
    switch (buildConfig) {
      case ConfigValues.USE_PROPERTIES: return CUSTOM;
      case ConfigValues.USE_HXML: return HXML;
      case ConfigValues.USE_NMML: return NMML;
      case ConfigValues.USE_OPENFL: return OPENFL;
      default:
        String msg = "Unknown build configuration type.";
        LOG.debug(msg);
        throw new InvalidDataException(msg);
    }
  }
}
