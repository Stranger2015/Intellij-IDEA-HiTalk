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
package com.intellij.plugins.logtalk.ide.completion;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.plugins.logtalk.lang.psi.LogtalkReference;
import com.intellij.psi.PsiElement;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class LogtalkCommonCompletionPattern {
  public static final PsiElementPattern.Capture<PsiElement> idInExpression =
    psiElement().withSuperParent(1, (Class<? extends PsiElement>)LogtalkIdentifier.class).withSuperParent(2, LogtalkReference.class);
  public static final PsiElementPattern.Capture<PsiElement> inComplexExpression =
    psiElement().withSuperParent(2, psiElement().withFirstChild(StandardPatterns.instanceOf(LogtalkReference.class)));
  public static final PsiElementPattern.Capture<PsiElement> isSimpleIdentifier =
    psiElement().andOr(StandardPatterns.instanceOf(LogtalkType.class), idInExpression.andNot(inComplexExpression));

  public static final PsiElementPattern.Capture<PsiElement> matchUsingAndImport = psiElement().andOr(
    StandardPatterns.instanceOf(LogtalkUsesStatement.class),
    StandardPatterns.instanceOf(LogtalkImportStatement.class));

  public static final PsiElementPattern.Capture<PsiElement> inImportOrUsing = psiElement().withSuperParent(3, matchUsingAndImport);
}
