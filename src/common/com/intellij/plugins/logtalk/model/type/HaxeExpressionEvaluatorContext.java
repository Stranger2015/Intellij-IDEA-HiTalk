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
package com.intellij.plugins.logtalk.model.type;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.plugins.haxe.ide.highlight.LogtalkSyntaxHighlighterColors;
import com.intellij.plugins.haxe.model.LogtalkDocumentModel;
import com.intellij.plugins.haxe.model.fixer.LogtalkFixer;
import com.intellij.plugins.logtalk.ide.highlight.LogtalkSyntaxHighlighterColors;
import com.intellij.plugins.logtalk.model.LogtalkDocumentModel;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LogtalkExpressionEvaluatorContext {
  public ResultHolder result;
  private List<ResultHolder> returns = new ArrayList<ResultHolder>();
  private List<PsiElement> returnElements = new ArrayList<PsiElement>();
  private List<ReturnInfo> returnInfos = new ArrayList<ReturnInfo>();

  public AnnotationHolder holder;
  private LogtalkScope<ResultHolder> scope = new LogtalkScope<ResultHolder>();
  public PsiElement root;

  public LogtalkExpressionEvaluatorContext(@NotNull PsiElement body, @Nullable AnnotationHolder holder) {
    this.root = body;
    this.holder = holder;
  }

  public LogtalkExpressionEvaluatorContext createChild(PsiElement body) {
    LogtalkExpressionEvaluatorContext that = new LogtalkExpressionEvaluatorContext(body, this.holder);
    that.scope = this.scope;
    that.beginScope();
    return that;
  }

  public void addReturnType(ResultHolder type, PsiElement element) {
    this.returns.add(type);
    this.returnElements.add(element);
    this.returnInfos.add(new ReturnInfo(element, type));
  }

  public ResultHolder getReturnType() {
    if (returns.isEmpty()) return SpecificLogtalkClassReference.getVoid(root).createHolder();
    return LogtalkTypeUnifier.unify(ResultHolder.types(returns), root).createHolder();
  }

  public List<ResultHolder> getReturnValues() {
    return returns;
  }

  public List<ReturnInfo> getReturnInfos() {
    return returnInfos;
  }

  public List<PsiElement> getReturnElements() {
    return returnElements;
  }

  public LogtalkDocumentModel getDocument() {
    return LogtalkDocumentModel.fromElement(root);
  }

  public void beginScope() {
    scope = new LogtalkScope<ResultHolder>(scope);
  }

  public void endScope() {
    scope = scope.parent;
  }

  public void setLocal(String key, ResultHolder value) {
    this.scope.set(key, value);
  }

  public void setLocalWhereDefined(String key, ResultHolder value) {
    this.scope.setWhereDefined(key, value);
  }

  public boolean has(String key) {
    return this.scope.has(key);
  }

  public ResultHolder get(String key) {
    return this.scope.get(key);
  }

  @NotNull
  public Annotation addError(PsiElement element, String error, LogtalkFixer... fixers) {
    if (holder == null) return createDummyAnnotation();
    Annotation annotation = holder.createErrorAnnotation(element, error);
    for (LogtalkFixer fixer : fixers) {
      annotation.registerFix(fixer);
    }
    return annotation;
  }

  @NotNull
  public Annotation addWarning(PsiElement element, String error, LogtalkFixer... fixers) {
    if (holder == null) return createDummyAnnotation();
    Annotation annotation = holder.createWarningAnnotation(element, error);
    for (LogtalkFixer fixer : fixers) {
      annotation.registerFix(fixer);
    }
    return annotation;
  }

  private Annotation createDummyAnnotation() {
    return new Annotation(0, 0, HighlightSeverity.ERROR, "", "");
  }

  @NotNull
  public Annotation addUnreachable(PsiElement element) {
    if (holder == null) return createDummyAnnotation();
    Annotation annotation = holder.createInfoAnnotation(element, null);
    annotation.setTextAttributes(LogtalkSyntaxHighlighterColors.LINE_COMMENT);
    return annotation;
  }

  final public List<LogtalkExpressionEvaluatorContext> lambdas = new LinkedList<LogtalkExpressionEvaluatorContext>();
  public void addLambda(LogtalkExpressionEvaluatorContext child) {
    lambdas.add(child);
  }
}

class ReturnInfo {
  final public ResultHolder type;
  final public PsiElement element;

  public ReturnInfo(PsiElement element, ResultHolder type) {
    this.element = element;
    this.type = type;
  }
}