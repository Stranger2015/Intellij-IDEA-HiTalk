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

import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkQualifiedNameProvider implements QualifiedNameProvider {
  @Override
  public PsiElement adjustElementToCopy(PsiElement element) {
    if (element instanceof LogtalkCallExpression) {
      element = ((LogtalkCallExpression)element).getExpression();
    }
    if (element instanceof LogtalkReference) {
      element = ((LogtalkReference)element).resolve();
    }
    if (element instanceof LogtalkComponentName) {
      return element.getParent();
    }
    return element;
  }

  @Override
  public String getQualifiedName(PsiElement element) {
    if (element instanceof LogtalkClass) {
      return ((LogtalkClass)element).getQualifiedName();
    }
    final LogtalkComponentType componentType = LogtalkComponentType.typeOf(element);
    if (componentType == LogtalkComponentType.METHOD || componentType == LogtalkComponentType.FIELD) {
      final String name = ((LogtalkComponent)element).getName();
      final LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(element, LogtalkClass.class, true);
      if (name != null && haxeClass != null) {
        return haxeClass.getQualifiedName() + "#" + name;
      }
    }
    return null;
  }

  @Nullable
  @Override
  public PsiElement qualifiedNameToElement(String fqn, Project project) {
    final int index = fqn.indexOf("#");
    if (index == -1) {
      final LogtalkClass haxeClass =
        LogtalkResolveUtil.findClassByQName(fqn, PsiManager.getInstance(project), GlobalSearchScope.projectScope(project));
      return haxeClass == null ? null : haxeClass.getComponentName();
    }
    final LogtalkClass haxeClass =
      LogtalkResolveUtil.findClassByQName(fqn.substring(0, index), PsiManager.getInstance(project), GlobalSearchScope.projectScope(project));
    if (haxeClass == null) {
      return null;
    }
    final String memberName = fqn.substring(index + 1);
    LogtalkNamedComponent namedComponent = haxeClass.findLogtalkMethodByName(memberName);
    if (namedComponent == null) {
      namedComponent = haxeClass.findLogtalkFieldByName(memberName);
    }
    return namedComponent == null ? null : namedComponent.getComponentName();
  }

  @Override
  public void insertQualifiedName(String fqn, PsiElement element, Editor editor, Project project) {
    EditorModificationUtil.insertStringAtCaret(editor, fqn);
  }
}
