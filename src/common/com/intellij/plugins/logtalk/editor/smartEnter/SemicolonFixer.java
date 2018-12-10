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
package com.intellij.plugins.logtalk.editor.smartEnter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;

/**
 * Created by as3boyan on 07.10.14.
 */
public class SemicolonFixer implements Fixer {
  @Override
  public void apply(Editor editor, LogtalkSmartEnterProcessor processor, PsiElement psiElement) throws IncorrectOperationException {
    if (fixReturn(editor, psiElement) || fixAfterLastValidElement(editor, psiElement)) {
      processor.setSkipEnter(true);
    }
  }

  private static boolean fixReturn(@NotNull Editor editor, @Nullable PsiElement psiElement) {
    if (!(psiElement instanceof LogtalkReturnStatement)) {
      return false;
    }

    LogtalkReturnStatement haxeReturnStatement = (LogtalkReturnStatement)psiElement;

    if (StringUtil.endsWithChar(haxeReturnStatement.getText(), ';')) {
      return false;
    }

    LogtalkFunctionDeclarationWithAttributes haxeFunctionDeclarationWithAttributes =
      PsiTreeUtil.getParentOfType(haxeReturnStatement, LogtalkFunctionDeclarationWithAttributes.class);

    if (haxeFunctionDeclarationWithAttributes != null) {
      LogtalkTypeTag typeTag = haxeFunctionDeclarationWithAttributes.getTypeTag();
      if (typeTag != null) {
        LogtalkTypeOrAnonymous typeOrAnonymous = getFirstItem(typeTag.getTypeOrAnonymousList());
        if (typeOrAnonymous != null) {
          if (typeOrAnonymous.getText().equals("Void")) {
            return false;
          }
        }
      }
    }

    LogtalkExpression haxeReturnStatementExpression = haxeReturnStatement.getExpression();
    if (haxeReturnStatementExpression != null) {
      Document doc = editor.getDocument();
      int offset = haxeReturnStatementExpression.getTextRange().getEndOffset();
      doc.insertString(offset, ";");
      editor.getCaretModel().moveToOffset(offset + 1);
      return true;
    }

    return false;
  }

  private static boolean fixAfterLastValidElement(@NotNull Editor editor, @Nullable PsiElement psiElement) {
    if (psiElement == null ||
        !(psiElement instanceof LogtalkExpression) &&
        !(psiElement instanceof LogtalkImportStatement) &&
        !(psiElement instanceof LogtalkBreakStatement) &&
        !(psiElement instanceof LogtalkContinueStatement) &&
        !(psiElement instanceof LogtalkReferenceExpression)) {
      return false;
    }

    if (StringUtil.endsWithChar(psiElement.getText(), ';')) {
      return false;
    }

    Document doc = editor.getDocument();
    int offset = psiElement.getTextRange().getEndOffset();
    doc.insertString(offset, ";");
    editor.getCaretModel().moveToOffset(offset + 1);
    return true;
  }


}
