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
package com.intellij.plugins.logtalk.ide.refactoring.introduceField;

import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.ide.refactoring.introduce.LogtalkIntroduceHandler;
import com.intellij.plugins.haxe.ide.refactoring.introduce.LogtalkIntroduceOperation;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkClassBody;
import com.intellij.plugins.haxe.lang.psi.LogtalkExpression;
import com.intellij.plugins.haxe.lang.psi.LogtalkVarDeclaration;
import com.intellij.plugins.haxe.util.LogtalkElementGenerator;
import com.intellij.plugins.logtalk.ide.refactoring.introduce.LogtalkIntroduceHandler;
import com.intellij.plugins.logtalk.ide.refactoring.introduce.LogtalkIntroduceOperation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by as3boyan on 12.09.14.
 */
public class LogtalkIntroduceConstantHandler extends LogtalkIntroduceHandler {

  public LogtalkIntroduceConstantHandler() {
    super("Introduce Constant");
  }

  @Override
  protected PsiElement addDeclaration(@NotNull final PsiElement expression,
                                      @NotNull final PsiElement declaration,
                                      @NotNull LogtalkIntroduceOperation operation) {
    return doIntroduceVariable(expression, declaration, operation.getOccurrences(), operation.isReplaceAll());
  }

  public static PsiElement doIntroduceVariable(PsiElement expression,
                                               PsiElement declaration,
                                               List<PsiElement> occurrences,
                                               boolean replaceAll) {
    //PsiElement anchor = replaceAll ? findAnchor(occurrences) : findAnchor(expression);
    //assert anchor != null;
    //final PsiElement parent = anchor.getParent();
    //return parent.addBefore(declaration, anchor);
    LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(expression, LogtalkClass.class, false);
    if (haxeClass != null) {
      //haxeClass.getVarDeclarations().get(0)
      LogtalkClassBody classBody = PsiTreeUtil.getChildOfType(haxeClass, LogtalkClassBody.class);

      if (classBody != null) {
        PsiElement child = classBody.getFirstChild();

        if (child != null) {
          return classBody.addBefore(declaration, child);
        }
        else {
          classBody.add(declaration);
        }
      }
    }

    return null;
  }

  @Nullable
  @Override
  public PsiElement createDeclaration(LogtalkIntroduceOperation operation) {
    final Project project = operation.getProject();
    final LogtalkExpression initializer = operation.getInitializer();
    InitializerTextBuilder builder = new InitializerTextBuilder();
    initializer.accept(builder);
    String assignmentText = "public static inline var " + operation.getName() + " = " + builder.result() + ";";
    PsiElement anchor = operation.isReplaceAll()
                        ? findAnchor(operation.getOccurrences())
                        : findAnchor(initializer);
    return createDeclaration(project, assignmentText, anchor);
  }

  private static class InitializerTextBuilder extends PsiRecursiveElementVisitor {
    private final StringBuilder myResult = new StringBuilder();

    @Override
    public void visitWhiteSpace(PsiWhiteSpace space) {
      myResult.append(space.getText().replace('\n', ' '));
    }

    @Override
    public void visitElement(PsiElement element) {
      if (element.getChildren().length == 0) {
        myResult.append(element.getText());
      }
      else {
        super.visitElement(element);
      }
    }

    public String result() {
      return myResult.toString();
    }
  }

  @Nullable
  @Override
  protected LogtalkVarDeclaration createDeclaration(Project project, String text, PsiElement anchor) {
    return LogtalkElementGenerator.createVarDeclaration(project, text);
  }
}
