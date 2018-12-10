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
package com.intellij.plugins.logtalk.ide.refactoring;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.lang.psi.impl.ComponentNameScopeProcessor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkRefactoringUtil {
  public static Set<String> collectUsedNames(LogtalkPsiCompositeElement context) {
    final Set<LogtalkComponentName> usedComponentNames = new THashSet<LogtalkComponentName>();
    PsiTreeUtil.treeWalkUp(new ComponentNameScopeProcessor(usedComponentNames), context, null, new ResolveState());
    return new THashSet<String>(ContainerUtil.map(usedComponentNames, new Function<LogtalkComponentName, String>() {
      @Nullable
      @Override
      public String fun(LogtalkComponentName componentName) {
        return componentName.getName();
      }
    }));
  }

  @Nullable
  public static LogtalkExpression getSelectedExpression(@NotNull final Project project,
                                                     @NotNull PsiFile file,
                                                     @NotNull final PsiElement element1,
                                                     @NotNull final PsiElement element2) {
    PsiElement parent = PsiTreeUtil.findCommonParent(element1, element2);
    if (parent == null) {
      return null;
    }
    if (parent instanceof LogtalkExpression) {
      return (LogtalkExpression)parent;
    }
    return PsiTreeUtil.getParentOfType(parent, LogtalkExpression.class);
  }

  @NotNull
  public static List<PsiElement> getOccurrences(@NotNull final PsiElement pattern, @Nullable final PsiElement context) {
    if (context == null) {
      return Collections.emptyList();
    }
    final List<PsiElement> occurrences = new ArrayList<PsiElement>();
    final LogtalkVisitor visitor = new LogtalkVisitor() {
      public void visitElement(@NotNull final PsiElement element) {
        if (element instanceof LogtalkParameter) {
          return;
        }
        if (PsiEquivalenceUtil.areElementsEquivalent(element, pattern)) {
          occurrences.add(element);
          return;
        }
        element.acceptChildren(this);
      }
    };
    context.acceptChildren(visitor);
    return occurrences;
  }

  @Nullable
  public static PsiElement findOccurrenceUnderCaret(List<PsiElement> occurrences, Editor editor) {
    if (occurrences.isEmpty()) {
      return null;
    }
    int offset = editor.getCaretModel().getOffset();
    for (PsiElement occurrence : occurrences) {
      if (occurrence.getTextRange().contains(offset)) {
        return occurrence;
      }
    }
    int line = editor.getDocument().getLineNumber(offset);
    for (PsiElement occurrence : occurrences) {
      if (occurrence.isValid() && editor.getDocument().getLineNumber(occurrence.getTextRange().getStartOffset()) == line) {
        return occurrence;
      }
    }
    for (PsiElement occurrence : occurrences) {
      if (occurrence.isValid()) {
        return occurrence;
      }
    }
    return null;
  }
}
