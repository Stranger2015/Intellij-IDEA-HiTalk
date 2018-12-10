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
package com.intellij.plugins.logtalk.ide;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkSmartCompletionContributor extends CompletionContributor {
  public LogtalkSmartCompletionContributor() {
    final PsiElementPattern.Capture<PsiElement> idInExpression =
      psiElement().withSuperParent(1, LogtalkIdentifier.class).withSuperParent(2, LogtalkReference.class);
    extend(CompletionType.SMART,
           idInExpression.and(psiElement().withSuperParent(3, LogtalkVarInit.class)),
           new CompletionProvider<CompletionParameters>() {
             @Override
             protected void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet result) {
               tryAddVariantsForEnums(result, parameters.getPosition());
             }
           });
  }

  private static void tryAddVariantsForEnums(CompletionResultSet result, @NotNull PsiElement element) {
    final LogtalkVarInit varInit = PsiTreeUtil.getParentOfType(element, LogtalkVarInit.class);
    assert varInit != null;
    final LogtalkClassResolveResult resolveResult =
      LogtalkResolveUtil.tryResolveClassByTypeTag(varInit.getParent(), LogtalkGenericSpecialization.EMPTY);
    final LogtalkClass haxeClass = resolveResult.getLogtalkClass();
    if (haxeClass instanceof LogtalkEnumDeclaration) {
      final String className = haxeClass.getName();
      for (LogtalkNamedComponent component : LogtalkResolveUtil.getNamedSubComponents(haxeClass)) {
        result.addElement(LookupElementBuilder.create(className + "." + component.getName()));
      }
    }
  }
}
