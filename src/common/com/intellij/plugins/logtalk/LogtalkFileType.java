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
package com.intellij.plugins.logtalk;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LogtalkFileType extends LanguageFileType {
  public static final LogtalkFileType LOGTALK_FILE_TYPE = new LogtalkFileType();

  @NonNls
  public static final String DEFAULT_EXTENSION = "lgt";

  private LogtalkFileType() {
    super(LogtalkLanguage.INSTANCE);
  }

  @NotNull
  @NonNls
  public String getName() {
    return LogtalkBundle.message("logtalk.file.type.name");
  }

  @NonNls
  @NotNull
  public String getDescription() {
    return LogtalkBundle.message("logtalk.file.type.description");
  }

  @NotNull
  @NonNls
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  public Icon getIcon() {
    return icons.LogtalkIcons.Logtalk_16;
  }

  @Override
  public String getCharset(@NotNull VirtualFile file, byte[] content) {
    return CharsetToolkit.UTF8;
  }
}
