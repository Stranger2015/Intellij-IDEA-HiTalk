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

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.plugins.haxe.ide.refactoring.extractInterface.ExtractInterfaceHandler;
import com.intellij.plugins.haxe.ide.refactoring.extractSuperclass.ExtractSuperclassHandler;
import com.intellij.plugins.haxe.ide.refactoring.introduce.LogtalkIntroduceVariableHandler;
import com.intellij.plugins.haxe.ide.refactoring.introduceField.LogtalkIntroduceConstantHandler;
import com.intellij.plugins.haxe.ide.refactoring.memberPullUp.LogtalkPullUpHandler;
import com.intellij.plugins.haxe.ide.refactoring.memberPushDown.LogtalkPushDownHandler;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedElement;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.Nullable;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkRefactoringSupportProvider extends RefactoringSupportProvider {
  @Override
  public boolean isInplaceRenameAvailable(PsiElement element, PsiElement context) {
    return element instanceof LogtalkNamedElement;
  }

  @Override
  public RefactoringActionHandler getIntroduceVariableHandler() {
    return new LogtalkIntroduceVariableHandler();
  }

  @Nullable
  @Override
  public RefactoringActionHandler getExtractInterfaceHandler() {
    return new ExtractInterfaceHandler();
  }

  @Nullable
  @Override
  public RefactoringActionHandler getExtractSuperClassHandler() {
    return new ExtractSuperclassHandler();
  }

  @Nullable
  @Override
  public RefactoringActionHandler getExtractClassHandler() {
    return super.getExtractClassHandler();
  }

  @Nullable
  @Override
  public RefactoringActionHandler getExtractMethodHandler() {
    return super.getExtractMethodHandler();
  }

  @Nullable
  @Override
  public RefactoringActionHandler getIntroduceConstantHandler() {
    return new LogtalkIntroduceConstantHandler();
  }

  @Nullable
  @Override
  public RefactoringActionHandler getPullUpHandler() {
    return new LogtalkPullUpHandler();
  }

  @Nullable
  @Override
  public RefactoringActionHandler getPushDownHandler() {
    return new LogtalkPushDownHandler();
  }
}