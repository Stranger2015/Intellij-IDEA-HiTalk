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
package com.intellij.plugins.logtalk.lang.psi;

import com.google.common.collect.Lists;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.plugins.haxe.LogtalkFileType;
import com.intellij.plugins.haxe.LogtalkLanguage;
import com.intellij.plugins.haxe.ide.hierarchy.LogtalkHierarchyUtils;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.model.LogtalkFileModel;
import com.intellij.plugins.haxe.util.LogtalkElementGenerator;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkFileType;
import com.intellij.plugins.logtalk.LogtalkLanguage;
import com.intellij.plugins.logtalk.ide.hierarchy.LogtalkHierarchyUtils;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogtalkFile extends PsiFileBase
  implements LogtalkModifierListOwner, PsiClassOwner {

  public LogtalkFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, LogtalkLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return LogtalkFileType.HAXE_FILE_TYPE;
  }

  @Override
  public String toString() {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      // Unit tests expect the fixed string.  Maybe we should fix the test goldens, then?
      return "Logtalk File";
    }
    return getName();
  }

  @Override
  public Icon getIcon(int flags) {
    return super.getIcon(flags);
  }

  @Override
  public PsiReference findReferenceAt(int offset) {
    return super.findReferenceAt(offset);
  }

  @Override
  public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
    final String oldName = FileUtil.getNameWithoutExtension(getName());
    final PsiElement result = super.setName(newName);
    final LogtalkClass haxeClass = LogtalkResolveUtil.findComponentDeclaration(this, oldName);
    if (haxeClass != null) {
      haxeClass.setName(FileUtil.getNameWithoutExtension(newName));
    }
    return result;
  }

  @Nullable
  @Override
  public LogtalkModifierList getModifierList() {
    // usually files don't have annotations or modifiers associated with them
    return null;
  }

  @Override
  public boolean hasModifierProperty(@PsiModifier.ModifierConstant @NonNls @NotNull String name) {
    // usually files don't have annotations or modifiers associated with them
    return false;
  }

  @NotNull
  @Override
  public PsiClass[] getClasses() {
    return LogtalkHierarchyUtils.getClassList(this);
  }

  public PsiPackageStatement getPackageStatement() {
    ASTNode node = calcTreeElement().findChildByType(LogtalkTokenTypes.PACKAGE_STATEMENT);
    return node != null ? (PsiPackageStatement)node.getPsi() : null;
  }

  @Override
  public String getPackageName() {
    PsiPackageStatement statement = getPackageStatement();
    return statement == null ? "" : statement.getPackageName();
  }

  @Override
  public void setPackageName(String packageName) throws IncorrectOperationException {
    // TODO: verify
    LogtalkPackageStatement packageStatementFromPath = LogtalkElementGenerator.createPackageStatementFromPath(getProject(), packageName);

    LogtalkPackageStatement packageStatement = PsiTreeUtil.getChildOfType(this, LogtalkPackageStatement.class);
    if (packageStatement != null) {
      packageStatement.replace(packageStatementFromPath);
    }
    else {
      addBefore(packageStatementFromPath, getFirstChild());
    }
  }

  public List<LogtalkImportStatement> getImportStatements() {
    LogtalkImportStatement[] result = PsiTreeUtil.getChildrenOfType(this, LogtalkImportStatement.class);
    return result == null ? Collections.emptyList() : new ArrayList<>(Arrays.asList(result));
  }

  public LogtalkFileModel getModel() {
    return LogtalkFileModel.fromElement(this);
  }
}
