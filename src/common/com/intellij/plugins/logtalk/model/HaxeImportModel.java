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

import com.intellij.plugins.haxe.lang.psi.LogtalkIdentifier;
import com.intellij.plugins.haxe.lang.psi.LogtalkImportStatement;
import com.intellij.plugins.haxe.lang.psi.LogtalkReferenceExpression;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LogtalkImportModel implements LogtalkExposableModel {
  private final LogtalkImportStatement basePsi;

  public LogtalkImportModel(@NotNull LogtalkImportStatement importStatement) {
    this.basePsi = importStatement;
  }

  @NotNull
  @Override
  public FullyQualifiedInfo getQualifiedInfo() {
    LogtalkReferenceExpression referenceExpression = getReferenceExpression();
    return new FullyQualifiedInfo(referenceExpression);
  }

  public boolean hasAlias() {
    return basePsi.getAlias() != null;
  }

  public boolean hasWildcard() {
    return basePsi.getWildcard() != null;
  }

  @Nullable
  public String getAliasName() {
    LogtalkIdentifier alias = basePsi.getAlias();
    if (alias == null) return null;
    return alias.getText();
  }

  public LogtalkReferenceExpression getReferenceExpression() {
    return basePsi.getReferenceExpression();
  }

  @NotNull
  @Override
  public List<LogtalkModel> getExposedMembers() {
    FullyQualifiedInfo qualifiedInfo = getQualifiedInfo();
    List<LogtalkModel> result = null;
    if (hasWildcard()) {
      if (qualifiedInfo.memberName != null) return Collections.emptyList();

      if (qualifiedInfo.fileName != null && qualifiedInfo.className == null) {
        qualifiedInfo = new FullyQualifiedInfo(qualifiedInfo.packagePath, qualifiedInfo.fileName, qualifiedInfo.fileName, null);
      }
      List<LogtalkModel> items = LogtalkProjectModel.fromElement(basePsi).resolve(qualifiedInfo, basePsi.getResolveScope());
      if (items != null && items.size() > 0) {
        result = items.stream()
          .filter(model -> model instanceof LogtalkExposableModel)
          .flatMap(model -> ((LogtalkExposableModel)model).getExposedMembers().stream())
          .collect(Collectors.toList());
      }
    } else {
      if (hasAlias() && qualifiedInfo.fileName != null && qualifiedInfo.className == null) {
        qualifiedInfo = new FullyQualifiedInfo(qualifiedInfo.packagePath, qualifiedInfo.fileName, qualifiedInfo.fileName, null);
      }
      result = LogtalkProjectModel.fromElement(basePsi).resolve(qualifiedInfo, basePsi.getResolveScope());
      if (result != null && !result.isEmpty()) {
        LogtalkModel firstItem = result.get(0);
        if (firstItem instanceof LogtalkFileModel || firstItem instanceof LogtalkPackageModel) {
          result = ((LogtalkExposableModel)firstItem).getExposedMembers();
        }
      }
    }

    return result != null ? exposeEnumValues(result) : Collections.emptyList();
  }

  @NotNull
  private List<LogtalkModel> exposeEnumValues(@NotNull List<LogtalkModel> result) {
    result.addAll(
      result.stream()
        .filter(model -> model instanceof LogtalkEnumModel)
        .flatMap(model -> ((LogtalkEnumModel)model).getValues().stream())
        .collect(Collectors.toList())
    );

    return result;
  }

  @Nullable
  public PsiElement exposeByName(String name) {
    if (name == null) return null;

    if (hasWildcard()) {
      for (LogtalkModel exposedMember : getExposedMembers()) {
        if (Objects.equals(exposedMember.getName(), name)) return exposedMember.getBasePsi();
      }
    } else {
      if (getReferenceExpression() != null) {
        FullyQualifiedInfo qualifiedInfo = getQualifiedInfo();

        LogtalkModel member = getExposedMember(name);
        if (((equalsToAlias(name) || qualifiedInfo.equalsToNamedPart(name)) && member != null) || member != null) {
          return member.getBasePsi();
        }
      }
    }

    return null;
  }

  private boolean equalsToAlias(String name) {
    return hasAlias() && Objects.equals(getAliasName(), name);
  }

  private LogtalkModel getExposedMember(String name) {
    List<? extends LogtalkModel> members = getExposedMembers();
    if (members.isEmpty()) return null;
    if (hasAlias()) {
      return (Objects.equals(getAliasName(), name)) ? members.get(0) : null;
    }
    return members.stream()
      .filter(model -> model.getName().equals(name))
      .findFirst()
      .orElse(null);
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public PsiElement getBasePsi() {
    return this.basePsi;
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return null;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }
}
