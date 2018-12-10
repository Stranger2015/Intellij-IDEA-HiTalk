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
package com.intellij.plugins.logtalk.ide.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.ide.actions.LogtalkTypeAddImportIntentionAction;
import com.intellij.plugins.haxe.ide.index.LogtalkComponentIndex;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponent;
import com.intellij.plugins.haxe.lang.psi.LogtalkReferenceExpression;
import com.intellij.plugins.haxe.lang.psi.LogtalkType;
import com.intellij.plugins.haxe.lang.psi.LogtalkVisitor;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.ide.actions.LogtalkTypeAddImportIntentionAction;
import com.intellij.plugins.logtalk.ide.index.LogtalkComponentIndex;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkTypeAnnotator extends LogtalkVisitor implements Annotator {
  private AnnotationHolder myHolder = null;

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    assert myHolder == null;
    myHolder = holder;
    try {
      element.accept(this);
    }
    finally {
      myHolder = null;
    }
  }

  @Override
  public void visitType(@NotNull LogtalkType type) {
    super.visitType(type);
    final LogtalkReferenceExpression expression = type.getReferenceExpression();
    if (expression.resolve() != null) {
      return;
    }

    tryCreateAnnotation(expression);
  }

  @Override
  public void visitReferenceExpression(@NotNull LogtalkReferenceExpression expression) {
    super.visitReferenceExpression(expression);

    if (expression.resolve() == null) {
      tryCreateAnnotation(expression);
    }
  }

  private void tryCreateAnnotation(LogtalkReferenceExpression expression) {
    final GlobalSearchScope scope = LogtalkResolveUtil.getScopeForElement(expression);
    final List<LogtalkComponent> components =
      LogtalkComponentIndex.getItemsByName(expression.getText(), expression.getProject(), scope);
    if (!components.isEmpty()) {
      myHolder.createErrorAnnotation(expression, LogtalkBundle.message("logtalk.unresolved.type"))
        .registerFix(new LogtalkTypeAddImportIntentionAction(expression, components));
    }
  }
}
