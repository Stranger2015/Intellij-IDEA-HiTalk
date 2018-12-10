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

import com.intellij.codeInsight.daemon.DaemonBundle;
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.LogtalkLanguage;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponentName;
import com.intellij.plugins.haxe.lang.psi.LogtalkMethod;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.plugins.logtalk.LogtalkLanguage;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkGotoSuperHandler implements LanguageCodeInsightActionHandler {
  @Override
  public boolean isValidFor(Editor editor, PsiFile file) {
    return file.getLanguage() == LogtalkLanguage.INSTANCE;
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    final PsiElement at = file.findElementAt(editor.getCaretModel().getOffset());
    final LogtalkComponentName componentName = PsiTreeUtil.getParentOfType(at, LogtalkComponentName.class);

    final LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(at, LogtalkClass.class);
    final LogtalkNamedComponent namedComponent = componentName == null ? haxeClass : (LogtalkNamedComponent)componentName.getParent();
    if (at == null || haxeClass == null || namedComponent == null) return;

    final List<LogtalkClass> supers = LogtalkResolveUtil.tyrResolveClassesByQName(haxeClass.getLogtalkExtendsList());
    supers.addAll(LogtalkResolveUtil.tyrResolveClassesByQName(haxeClass.getLogtalkImplementsList()));
    final List<LogtalkNamedComponent> superItems = LogtalkResolveUtil.findNamedSubComponents(false, supers.toArray(new LogtalkClass[supers.size()]));

    final LogtalkComponentType type = LogtalkComponentType.typeOf(namedComponent);
    if (type == LogtalkComponentType.METHOD) {
      final LogtalkMethod methodDeclaration = (LogtalkMethod)namedComponent;
      tryNavigateToSuperMethod(editor, methodDeclaration, superItems);
    }
    else if (!supers.isEmpty() && namedComponent instanceof LogtalkClass) {
      PsiElementListNavigator.openTargets(
        editor,
        LogtalkResolveUtil.getComponentNames(supers).toArray(new NavigatablePsiElement[supers.size()]),
        DaemonBundle.message("navigation.title.subclass", namedComponent.getName(), supers.size()),
        "Subclasses of " + namedComponent.getName(),
        new DefaultPsiElementCellRenderer()
      );
    }
  }

  private static void tryNavigateToSuperMethod(Editor editor,
                                               LogtalkMethod methodDeclaration,
                                               List<LogtalkNamedComponent> superItems) {
    final String methodName = methodDeclaration.getName();
    if (methodName == null) {
      return;
    }
    final List<LogtalkNamedComponent> filteredSuperItems = ContainerUtil.filter(superItems, new Condition<LogtalkNamedComponent>() {
      @Override
      public boolean value(LogtalkNamedComponent component) {
        return methodName.equals(component.getName());
      }
    });
    if (!filteredSuperItems.isEmpty()) {
      PsiElementListNavigator.openTargets(editor, LogtalkResolveUtil.getComponentNames(filteredSuperItems)
        .toArray(new NavigatablePsiElement[filteredSuperItems.size()]),
                                          DaemonBundle.message("navigation.title.super.method", methodName),
                                          null,
                                          new DefaultPsiElementCellRenderer());
    }
  }

  @Override
  public boolean startInWriteAction() {
    return true;
  }
}
