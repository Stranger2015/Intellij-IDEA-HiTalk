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
package com.intellij.plugins.logtalk.util;

import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.LogtalkFileType;
import com.intellij.plugins.haxe.LogtalkLanguage;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.lang.psi.impl.LogtalkExpressionCodeFragmentImpl;
import com.intellij.plugins.logtalk.LogtalkFileType;
import com.intellij.plugins.logtalk.LogtalkLanguage;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.logtalk.lang.psi.LogtalkReference;
import com.intellij.plugins.logtalk.lang.psi.impl.LogtalkExpressionCodeFragmentImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkElementGenerator {

  public static PsiElement createExpressionFromText(Project myProject, String text) {
    PsiElement fromText = createStatementFromText(myProject, "var test = " + text + ";");
    if (fromText instanceof LogtalkVarDeclaration) {
      LogtalkVarDeclaration declarationPart = ((LogtalkVarDeclaration)fromText);
      LogtalkVarInit varInit = declarationPart.getVarInit();
      return varInit != null ? varInit.getExpression() : null;
    }
    return null;
  }

  public static PsiElement createStatementFromText(Project myProject, String text) {
    final PsiFile dummyFile = createDummyFile(myProject, LogtalkCodeGenerateUtil.wrapStatement(text).getFirst());
    final LogtalkClass haxeClass = PsiTreeUtil.getChildOfType(dummyFile, LogtalkClass.class);
    assert haxeClass != null;
    final LogtalkFunctionDeclarationWithAttributes mainMethod =
      (LogtalkFunctionDeclarationWithAttributes)haxeClass.getLogtalkMethods().iterator().next();
    final LogtalkBlockStatement statement = mainMethod.getBlockStatement();
    assert statement != null;
    return statement.getChildren()[0];
  }
  public static LogtalkVarDeclaration createVarDeclaration(Project myProject, String text) {
    final PsiFile dummyFile = createDummyFile(myProject, LogtalkCodeGenerateUtil.wrapFunction(text).getFirst());
    final LogtalkClass haxeClass = PsiTreeUtil.getChildOfType(dummyFile, LogtalkClass.class);
    assert haxeClass != null;
    return haxeClass.getVarDeclarations().iterator().next();
  }

  // XXX: Eventually, this ordering should come from the class order in
  //      preferences... once we have one.
  private static List<LogtalkNamedComponent> sortNamedSubComponents(List<LogtalkNamedComponent> unsorted) {
    // Can't sort a hashed collection, so we must copy it to an orderable type.
    List<LogtalkNamedComponent> sorted = new ArrayList<LogtalkNamedComponent>(unsorted);
    Collections.sort(sorted, new Comparator<LogtalkNamedComponent>() {
      @Override
      public int compare(LogtalkNamedComponent o1, LogtalkNamedComponent o2) {
        String name1 = o1.getName();
        String name2 = o2.getName();
        return name1.compareTo(name2);
      }
    });
    return sorted;
  }

  public static List<LogtalkNamedComponent> createNamedSubComponentsFromText(Project myProject, String text) {
    final PsiFile dummyFile = createDummyFile(myProject, LogtalkCodeGenerateUtil.wrapFunction(text).getFirst());
    final LogtalkClass haxeClass = PsiTreeUtil.getChildOfType(dummyFile, LogtalkClass.class);
    assert haxeClass != null;
    return sortNamedSubComponents(LogtalkResolveUtil.findNamedSubComponents(haxeClass));
  }

  @Nullable
  public static LogtalkIdentifier createIdentifierFromText(Project myProject, String name) {
    return createImportAndFindChild(myProject, name, LogtalkIdentifier.class);
  }

  @Nullable
  public static LogtalkReference createReferenceFromText(Project myProject, String name) {
    return createImportAndFindChild(myProject, name, LogtalkReference.class);
  }

  @Nullable
  public static LogtalkReferenceExpression createReferenceExpressionFromText(Project myProject, String name) {
    return createImportAndFindChild(myProject, name, LogtalkReferenceExpression.class);
  }

  @Nullable
  private static <T extends PsiElement> T createImportAndFindChild(Project myProject, String name, Class<T> aClass) {
    final LogtalkImportStatement importStatement = createImportStatementFromPath(myProject, name);
    if (importStatement == null) {
      return null;
    }
    return PsiTreeUtil.findChildOfType(importStatement, aClass);
  }

  @Nullable
  public static LogtalkImportStatement createImportStatementFromPath(Project myProject, String path) {
    final PsiFile dummyFile = createDummyFile(myProject, "import " + path + ";");
    return PsiTreeUtil.getChildOfType(dummyFile, LogtalkImportStatement.class);
  }

  @Nullable
  public static LogtalkPackageStatement createPackageStatementFromPath(Project myProject, String path) {
    final PsiFile dummyFile = createDummyFile(myProject, "package " + path + ";");
    return PsiTreeUtil.getChildOfType(dummyFile, LogtalkPackageStatement.class);
  }

  public static PsiFile createDummyFile(Project myProject, String text) {
    final PsiFileFactory factory = PsiFileFactory.getInstance(myProject);
    final String name = "dummy." + LogtalkFileType.HAXE_FILE_TYPE.getDefaultExtension();
    final LightVirtualFile virtualFile = new LightVirtualFile(name, LogtalkFileType.HAXE_FILE_TYPE, text);
    final PsiFile psiFile = ((PsiFileFactoryImpl)factory).trySetupPsiForFile(virtualFile, LogtalkLanguage.INSTANCE, false, true);
    assert psiFile != null;
    return psiFile;
  }

  public static PsiFile createExpressionCodeFragment(Project myProject, String text, PsiElement context, boolean resolveScope) {
    final String name = "dummy." + LogtalkFileType.HAXE_FILE_TYPE.getDefaultExtension();
    LogtalkExpressionCodeFragmentImpl codeFragment = new LogtalkExpressionCodeFragmentImpl(myProject, name, text, true);
    codeFragment.setContext(context);
    return codeFragment;
  }

  public static LogtalkFunctionPrototypeDeclarationWithAttributes createFunctionPrototypeDeclarationWithAttributes(Project myProject,
                                                                                                                String text) {
    final PsiFile dummyFile = createDummyFile(myProject, LogtalkCodeGenerateUtil.wrapInterfaceFunction(text).getFirst());
    final LogtalkClass haxeClass = PsiTreeUtil.getChildOfType(dummyFile, LogtalkClass.class);
    assert haxeClass != null;
    return (LogtalkFunctionPrototypeDeclarationWithAttributes)haxeClass.getLogtalkMethods().iterator().next();
  }

  public static LogtalkFunctionDeclarationWithAttributes createFunctionDeclarationWithAttributes(Project myProject, String text) {
    final PsiFile dummyFile = createDummyFile(myProject, LogtalkCodeGenerateUtil.wrapFunction(text).getFirst());
    final LogtalkClass haxeClass = PsiTreeUtil.getChildOfType(dummyFile, LogtalkClass.class);
    assert haxeClass != null;
    return (LogtalkFunctionDeclarationWithAttributes)haxeClass.getLogtalkMethods().iterator().next();
  }
}
