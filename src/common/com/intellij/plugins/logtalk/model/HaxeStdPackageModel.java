/*
 * Copyright 2017-2017 Ilya Malanin
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
package com.intellij.plugins.logtalk.model;

import com.intellij.plugins.haxe.lang.psi.LogtalkFile;
import com.intellij.plugins.logtalk.lang.psi.LogtalkFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LogtalkStdPackageModel extends LogtalkPackageModel {
  private static final String STD_TYPES = "StdTypes";
  private final LogtalkFileModel stdTypesModel;

  LogtalkStdPackageModel(@NotNull LogtalkSourceRootModel root) {
    super(root, "", null);
    this.stdTypesModel = this.getStdFileModel();
  }

  private LogtalkFileModel getStdFileModel() {
    final LogtalkFile file = getFile(STD_TYPES);
    if (file != null) {
      return new LogtalkStdTypesFileModel(file);
    }
    return null;
  }

  @Nullable
  @Override
  public LogtalkClassModel getClassModel(@NotNull String className) {
    LogtalkClassModel result = super.getClassModel(className);

    if (result == null && stdTypesModel != null) {
      result = stdTypesModel.getClassModel(className);
    }

    return result;
  }

  @Override
  public LogtalkModel resolve(FullyQualifiedInfo info) {
    LogtalkModel result = super.resolve(info);

    if (result == null && stdTypesModel != null && info.packagePath.isEmpty() && this.path.isEmpty()) {
      result = stdTypesModel.resolve(new FullyQualifiedInfo("", null, info.fileName, info.memberName));
    }

    return result;
  }
}
