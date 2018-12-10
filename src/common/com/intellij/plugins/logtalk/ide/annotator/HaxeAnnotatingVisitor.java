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

import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.LogtalkModifierType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public abstract class LogtalkAnnotatingVisitor extends LogtalkVisitor {
  private static final Set<String> BUILTIN = new THashSet<String>(Arrays.asList(
    "$type", "trace", "__call__", "__vmem_set__", "__vmem_get__", "__vmem_sign__", "__global__", "_global", "__foreach__"
  ));

  @Override
  public void visitReferenceExpression(@NotNull LogtalkReferenceExpression reference) {
    if (reference.getTokenType() != LogtalkTokenTypes.REFERENCE_EXPRESSION) {
      return; // call, array access, this, literal, etc
    }

    if (isInsidePackageStatement(reference) || isBuiltInMethod(reference)) return;

    checkDeprecatedVarCall(reference);

    if (reference.resolve() == null) {
      handleUnresolvedReference(reference);
    }

    super.visitReferenceExpression(reference);
  }

  private boolean isBuiltInMethod(@NotNull LogtalkReferenceExpression reference) {
    return BUILTIN.contains(reference.getReferenceName()) &&
           reference.getParent() instanceof LogtalkCallExpression &&
           !(reference.getParent().getParent() instanceof LogtalkReference);
  }

  private boolean isInsidePackageStatement(@NotNull LogtalkReferenceExpression reference) {
    return PsiTreeUtil.getParentOfType(reference, LogtalkPackageStatement.class) != null;
  }

  @Override
  public void visitFunctionDeclarationWithAttributes(@NotNull LogtalkFunctionDeclarationWithAttributes functionDeclaration) {
    List<LogtalkCustomMeta> metas = functionDeclaration.getCustomMetaList();
    for (LogtalkCustomMeta meta : metas) {
      if (isDeprecatedMeta(meta)) {
        handleDeprecatedFunctionDeclaration(functionDeclaration);
      }
    }

    super.visitFunctionDeclarationWithAttributes(functionDeclaration);
  }

  @Override
  public void visitCallExpression(@NotNull LogtalkCallExpression o) {
    final PsiElement child = o.getFirstChild();
    if (child instanceof LogtalkReferenceExpression) {
      LogtalkReferenceExpression referenceExpression = (LogtalkReferenceExpression)child;
      final PsiElement reference = referenceExpression.resolve();

      if (reference instanceof LogtalkFunctionDeclarationWithAttributes) {
        final LogtalkFunctionDeclarationWithAttributes functionDeclaration = (LogtalkFunctionDeclarationWithAttributes)reference;
        final List<LogtalkCustomMeta> metas = functionDeclaration.getCustomMetaList();
        for (LogtalkCustomMeta meta : metas) {
          if (isDeprecatedMeta(meta)) {
            handleDeprecatedCallExpression(referenceExpression);
          }
        }
      }
    }

    super.visitCallExpression(o);
  }

  @Override
  public void visitVarDeclaration(@NotNull LogtalkVarDeclaration varDeclaration) {
    List<LogtalkCustomMeta> metas = varDeclaration.getCustomMetaList();
    for (LogtalkCustomMeta meta : metas) {
      if (isDeprecatedMeta(meta)) {
        handleDeprecatedVarDeclaration(varDeclaration);
      }
    }

    super.visitVarDeclaration(varDeclaration);
  }

  @Override
  public void visitElement(PsiElement element) {
    ProgressIndicatorProvider.checkCanceled();
    element.acceptChildren(this);
  }

  protected void handleUnresolvedReference(LogtalkReferenceExpression reference) {
  }

  protected void handleDeprecatedFunctionDeclaration(LogtalkFunctionDeclarationWithAttributes functionDeclaration) {
  }

  protected void handleDeprecatedCallExpression(LogtalkReferenceExpression referenceExpression) {
  }

  protected void handleDeprecatedVarDeclaration(LogtalkVarDeclaration varDeclaration) {
  }

  private void checkDeprecatedVarCall(LogtalkReferenceExpression referenceExpression) {
    PsiElement reference = referenceExpression.resolve();

    if (reference instanceof LogtalkVarDeclaration) {
      LogtalkVarDeclaration varDeclaration = (LogtalkVarDeclaration)reference;

      List<LogtalkCustomMeta> metas = varDeclaration.getCustomMetaList();
      for (LogtalkCustomMeta meta : metas) {
        if (isDeprecatedMeta(meta)) {
          handleDeprecatedCallExpression(referenceExpression);
        }
      }
    }
  }

  private boolean isDeprecatedMeta(@NotNull LogtalkCustomMeta meta) {
    String metaText = meta.getText();
    return metaText != null && metaText.startsWith(LogtalkModifierType.DEPRECATED.s);
  }
}