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
package com.intellij.plugins.logtalk.ide.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.lang.psi.LogtalkImportStatement;
import com.intellij.plugins.haxe.model.LogtalkModel;
import com.intellij.plugins.haxe.model.LogtalkModelTarget;
import com.intellij.plugins.haxe.util.LogtalkElementGenerator;
import com.intellij.plugins.haxe.util.LogtalkImportUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by as3boyan on 04.10.14.
 */
public class ReplaceImportStatementWithWildcardWithSingleClassImports implements IntentionAction {
  @NotNull
  @Override
  public String getText() {
    return "Replace with single class imports";
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return getText();
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    PsiElement elementAt = file.findElementAt(editor.getCaretModel().getOffset());
    LogtalkImportStatement importStatement = PsiTreeUtil.getParentOfType(elementAt, LogtalkImportStatement.class);

    return importStatement != null &&
           importStatement.getWildcard() != null &&
           LogtalkImportUtil.getExternalReferences(file).stream()
             .anyMatch(element -> LogtalkImportUtil.isStatementExposesReference(importStatement, element));
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    PsiElement elementAt = file.findElementAt(editor.getCaretModel().getOffset());
    LogtalkImportStatement importStatement = PsiTreeUtil.getParentOfType(elementAt, LogtalkImportStatement.class);

    if (importStatement == null || importStatement.getWildcard() == null) return;

    List<PsiElement> newImports = LogtalkImportUtil.getExternalReferences(file).stream()
      .map(element -> LogtalkImportUtil.exposeReference(importStatement, element))
      .filter(Objects::nonNull)
      .distinct()
      .collect(Collectors.toList());

    newImports.forEach(elementToImport -> {
      if (elementToImport instanceof LogtalkModelTarget) {
        LogtalkModel model = ((LogtalkModelTarget)elementToImport).getModel();
        LogtalkImportStatement newImportStatement =
          LogtalkElementGenerator.createImportStatementFromPath(project, model.getQualifiedInfo().getPresentableText());
        if (newImportStatement != null) {
          file.addBefore(newImportStatement, importStatement);
        }
      }
    });

    importStatement.delete();
  }

  @Override
  public boolean startInWriteAction() {
    return true;
  }
}
