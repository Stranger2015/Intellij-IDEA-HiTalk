/*
 * Copyright 2017-2018 Ilya Malanin
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

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.LogtalkAddImportHelper;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkFile;
import com.intellij.plugins.logtalk.util.LogtalkAddImportHelper;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogtalkFileModel implements LogtalkExposableModel {

  private final LogtalkFile file;

  public LogtalkFileModel(@NotNull LogtalkFile file) {
    this.file = file;
  }

  @Nullable
  static public LogtalkFileModel fromElement(PsiElement element) {
    if (element == null) return null;

    final PsiFile file = element instanceof PsiFile ? (PsiFile)element : element.getContainingFile();
    if (file != null) {
      return new LogtalkFileModel((LogtalkFile)file);
    }
    return null;
  }

  @Override
  public PsiElement getBasePsi() {
    return this.file;
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return getPackageModel();
  }

  @Nullable
  @Override
  public FullyQualifiedInfo getQualifiedInfo() {
    LogtalkExposableModel container = getExhibitor();
    if (container != null) {
      FullyQualifiedInfo qualifiedInfo = container.getQualifiedInfo();
      if (qualifiedInfo != null) {
        return new FullyQualifiedInfo(qualifiedInfo.packagePath, getName(), null, null);
      }
    }
    return null;
  }

  @Nullable
  @Override
  public List<LogtalkModel> getExposedMembers() {
    return getClassModels().stream()
      .filter(LogtalkClassModel::isPublic)
      .collect(Collectors.toList());
  }

  @Nullable
  public LogtalkClassModel getMainClassModel() {
    return getClassModel(getName());
  }

  @Nullable
  public LogtalkClassModel getClassModel(String name) {
    LogtalkClass haxeClass = (LogtalkClass)Arrays.stream(file.getChildren())
      .filter(element -> element instanceof LogtalkClass && Objects.equals(name, ((LogtalkClass)element).getName()))
      .findFirst()
      .orElse(null);

    return haxeClass != null ? haxeClass.getModel() : null;
  }

  @NotNull
  public LogtalkFile getFile() {
    return file;
  }

  @NotNull
  public String getName() {
    return FileUtil.getNameWithoutExtension(file.getName());
  }

  @NotNull
  public String getFileName() {
    return file.getName();
  }

  @Nullable
  public LogtalkPackageStatement getPackagePsi() {
    return UsefulPsiTreeUtil.getChild(file, LogtalkPackageStatement.class);
  }

  @Nullable
  public String getPackageName() {
    LogtalkPackageStatement value = getPackagePsi();
    if (value != null) {
      String name = value.getPackageName();
      return name == null ? "" : name;
    }
    return detectPackageName();
  }

  public LogtalkProjectModel getProject() {
    return LogtalkProjectModel.fromElement(file);
  }

  public FullyQualifiedInfo getFullyQualifiedInfo() {
    return new FullyQualifiedInfo(getPackageName(), getName(), null, null);
  }

  public List<LogtalkClassModel> getClassModels() {
    return getClassModelsStream().collect(Collectors.toList());
  }

  public Stream<LogtalkClassModel> getClassModelsStream() {
    return Arrays.stream(file.getChildren())
      .filter(element -> element instanceof LogtalkClass)
      .map(element -> ((LogtalkClass)element).getModel());
  }

  public List<LogtalkImportStatement> getImportStatements() {
    return Arrays.stream(file.getChildren())
      .filter(element -> element instanceof LogtalkImportStatement)
      .map(element -> ((LogtalkImportStatement)element))
      .collect(Collectors.toList());
  }

  public List<LogtalkImportModel> getImportModels() {
    return Arrays.stream(file.getChildren())
      .filter(element -> element instanceof LogtalkImportStatement)
      .map(element -> ((LogtalkImportStatement)element).getModel())
      .collect(Collectors.toList());
  }

  public List<LogtalkUsingStatement> getUsingStatements() {
    return Arrays.stream(file.getChildren())
      .filter(element -> element instanceof LogtalkUsingStatement)
      .map(element -> (LogtalkUsingStatement)element)
      .collect(Collectors.toList());
  }

  public List<LogtalkUsingModel> getUsingModels() {
    return Arrays.stream(file.getChildren())
      .filter(element -> element instanceof LogtalkUsingStatement)
      .map(element -> ((LogtalkUsingStatement)element).getModel())
      .collect(Collectors.toList());
  }

  public LogtalkPackageModel getPackageModel() {
    LogtalkProjectModel project = LogtalkProjectModel.fromElement(file);
    LogtalkSourceRootModel result = project.getRoots().stream()
      .filter(model -> model.contains(file))
      .findFirst().orElse(null);

    if (result == null && project.getSdkRoot().contains(file)) {
      result = project.getSdkRoot();
    }

    if (result != null) {
      LogtalkModel model = result.resolve(getFullyQualifiedInfo().toPackageQualifiedName());
      if (model != null && model instanceof LogtalkPackageModel) {
        return (LogtalkPackageModel)model;
      }
    }

    return null;
  }

  public LogtalkModel resolve(FullyQualifiedInfo info) {
    if (isReferencingCurrentFile(info)) {
      if (info.className == null) return this;

      LogtalkClassModel classModel = getClassModel(info.className);
      if (classModel != null) {
        if (info.memberName != null) {
          return classModel.getMember(info.memberName);
        }
        return classModel;
      }
    }

    return null;
  }

  protected boolean isReferencingCurrentFile(FullyQualifiedInfo info) {
    return info.fileName != null && info.fileName.equals(getName());
  }

  private String detectPackageName() {
    LogtalkSourceRootModel sourceRootModel = getProject().getContainingRoot(file.getContainingFile().getParent());
    if (sourceRootModel != null) {
      return StringUtils.replace(sourceRootModel.resolvePath(file.getParent()), "/", ".");
    }

    return "";
  }

  public void replaceOrCreatePackageStatement(@NotNull LogtalkPackageStatement statement) {
    LogtalkPackageStatement currentPsi = getPackagePsi();
    if (currentPsi != null) {
      currentPsi.replace(statement);
    } else {
      file.addBefore(statement, file.getFirstChild());
    }
  }

  public LogtalkImportStatement addImport(String path) {
    // FIXME Move code of this helper inside model + add mode addImport methods
    return LogtalkAddImportHelper.addImport(path, this.file);
  }
}