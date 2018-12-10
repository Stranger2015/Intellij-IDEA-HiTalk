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

/**
 * @author: Fedor.Korotkov
 */
public interface LogtalkSdkAdditionalDataBase {
  String getHomePath();

  String getVersion();

  String getNekoBinPath();

  void setNekoBinPath(String nekoBinPath);

  String getLogtalklibPath();

  void setLogtalklibPath(String haxelibPath);

  boolean getUseCompilerCompletionFlag();

  void setUseCompilerCompletionFlag(boolean newState);

  boolean getRemoveCompletionDuplicatesFlag();

  void setRemoveCompletionDuplicatesFlag(boolean newState);

}
