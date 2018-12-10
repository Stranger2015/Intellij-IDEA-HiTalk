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
package com.intellij.plugins.logtalk.ide.surroundWith;

import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.lang.psi.LogtalkBlockStatement;
import com.intellij.plugins.haxe.lang.psi.LogtalkCatchStatement;
import com.intellij.plugins.haxe.lang.psi.LogtalkParameter;
import com.intellij.plugins.haxe.lang.psi.LogtalkTryStatement;
import com.intellij.plugins.haxe.lang.psi.impl.LogtalkStatementUtils;
import com.intellij.plugins.haxe.util.LogtalkElementGenerator;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkTryCatchSurrounder extends LogtalkManyStatementsSurrounder {
  @NotNull
  @Override
  protected PsiElement doSurroundElements(PsiElement[] elements, PsiElement parent) {
    final LogtalkTryStatement tryStatement =
      (LogtalkTryStatement)LogtalkElementGenerator.createStatementFromText(elements[0].getProject(), "try {\n} catch(a) {\n}");
    List<LogtalkBlockStatement> blockStatementList = tryStatement.getBlockStatementList();
    addStatements(blockStatementList.get(0), elements);
    return tryStatement;
  }

  @Override
  protected TextRange getSurroundSelectionRange(PsiElement element) {
    final LogtalkCatchStatement catchStatement = LogtalkStatementUtils.getCatchStatement(element);
    final LogtalkParameter parameter = catchStatement.getParameter();
    return parameter == null ? catchStatement.getTextRange() : parameter.getTextRange();
  }

  @Override
  public String getTemplateDescription() {
    return LogtalkBundle.message("logtalk.surrounder.try.catch");
  }
}
