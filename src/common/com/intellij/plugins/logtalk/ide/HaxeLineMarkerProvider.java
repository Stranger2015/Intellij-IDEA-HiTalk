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

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.DaemonBundle;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.Condition;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.ide.index.LogtalkInheritanceDefinitionsSearchExecutor;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkLineMarkerProvider implements LineMarkerProvider {

  @Override
  public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
    return null;
  }

  @Override
  public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    for (PsiElement element : elements) {
      if (element instanceof LogtalkClass) {
        collectClassMarkers(result, (LogtalkClass)element);
      }
    }
  }

  private static void collectClassMarkers(Collection<LineMarkerInfo> result, @NotNull LogtalkClass haxeClass) {
    final List<LogtalkClass> supers = LogtalkResolveUtil.tyrResolveClassesByQName(haxeClass.getLogtalkExtendsList());
    supers.addAll(LogtalkResolveUtil.tyrResolveClassesByQName(haxeClass.getLogtalkImplementsList()));
    final List<LogtalkNamedComponent> superItems = LogtalkResolveUtil.findNamedSubComponents(supers.toArray(new LogtalkClass[supers.size()]));

    final List<LogtalkClass> subClasses = LogtalkInheritanceDefinitionsSearchExecutor.getItemsByQName(haxeClass);
    final List<LogtalkNamedComponent> subItems = new ArrayList<>();
    for (LogtalkClass subClass : subClasses) {
      subItems.addAll(LogtalkResolveUtil.getNamedSubComponents(subClass));
    }

    final boolean isInterface = LogtalkComponentType.typeOf(haxeClass) == LogtalkComponentType.INTERFACE;
    for (LogtalkNamedComponent haxeNamedComponent : LogtalkResolveUtil.getNamedSubComponents(haxeClass)) {
      final LogtalkComponentType type = LogtalkComponentType.typeOf(haxeNamedComponent);
      if (type == LogtalkComponentType.METHOD || type == LogtalkComponentType.FIELD) {
        LineMarkerInfo item = tryCreateOverrideMarker(haxeNamedComponent, superItems);
        if (item != null) {
          result.add(item);
        }
        item = tryCreateImplementationMarker(haxeNamedComponent, subItems, isInterface);
        if (item != null) {
          result.add(item);
        }
      }
    }

    if (!subClasses.isEmpty()) {
      final LineMarkerInfo marker = createImplementationMarker(haxeClass, subClasses);
      if (marker != null) {
        result.add(marker);
      }
    }
  }

  @Nullable
  private static LineMarkerInfo tryCreateOverrideMarker(final LogtalkNamedComponent namedComponent,
                                                        List<LogtalkNamedComponent> superItems) {

    final LogtalkComponentName componentName = namedComponent.getComponentName();
    final String methodName = namedComponent.getName();
    if (componentName == null || methodName == null || methodName.isEmpty()) {
      return null;
    }

    final List<LogtalkNamedComponent> filteredSuperItems = ContainerUtil.filter(superItems, item -> methodName.equals(item.getName()));
    if (filteredSuperItems.isEmpty()) {
      return null;
    }
    final PsiElement element = componentName.getIdentifier().getFirstChild();
    LogtalkComponentWithDeclarationList componentWithDeclarationList = namedComponent instanceof LogtalkComponentWithDeclarationList ?
                                                                    (LogtalkComponentWithDeclarationList)namedComponent : null;
    final boolean overrides = componentWithDeclarationList != null &&
                              LogtalkResolveUtil.getDeclarationTypes(componentWithDeclarationList.getDeclarationAttributeList()).
                                contains(LogtalkTokenTypes.KOVERRIDE);
    final Icon icon = overrides ? AllIcons.Gutter.OverridingMethod : AllIcons.Gutter.ImplementingMethod;
    if (null == element) {
      return null;
    }
    return new LineMarkerInfo<>(
      element,
      element.getTextRange(),
      icon,
      Pass.UPDATE_ALL,
      new Function<PsiElement, String>() {
        @Override
        public String fun(PsiElement element) {
          final LogtalkClass superLogtalkClass = PsiTreeUtil.getParentOfType(namedComponent, LogtalkClass.class);
          if (superLogtalkClass == null) return "null";
          if (overrides) {
            return LogtalkBundle.message("overrides.method.in", namedComponent.getName(), superLogtalkClass.getQualifiedName());
          }
          return LogtalkBundle.message("implements.method.in", namedComponent.getName(), superLogtalkClass.getQualifiedName());
        }
      },
      new GutterIconNavigationHandler<PsiElement>() {
        @Override
        public void navigate(MouseEvent e, PsiElement elt) {
          PsiElementListNavigator.openTargets(
            e,
            LogtalkResolveUtil.getComponentNames(filteredSuperItems).toArray(new NavigatablePsiElement[filteredSuperItems.size()]),
            DaemonBundle.message("navigation.title.super.method", namedComponent.getName()),
            DaemonBundle.message("navigation.findUsages.title.super.method", namedComponent.getName()),
            new DefaultPsiElementCellRenderer());
        }
      },
      GutterIconRenderer.Alignment.LEFT
    );
  }

  @Nullable
  private static LineMarkerInfo tryCreateImplementationMarker(final LogtalkNamedComponent namedComponent,
                                                              List<LogtalkNamedComponent> subItems,
                                                              final boolean isInterface) {
    final LogtalkComponentName componentName = namedComponent.getComponentName();
    final String methodName = namedComponent.getName();
    if (componentName == null || methodName == null || methodName.isEmpty()) {
      return null;
    }

    final List<LogtalkNamedComponent> filteredSubItems = ContainerUtil.filter(subItems, item -> methodName.equals(item.getName()));
    if (filteredSubItems.isEmpty()) {
      return null;
    }
    final PsiElement element = componentName.getIdentifier().getFirstChild();
    return new LineMarkerInfo<>(
      element,
      element.getTextRange(),
      isInterface ? AllIcons.Gutter.ImplementedMethod : AllIcons.Gutter.OverridenMethod,
      Pass.UPDATE_ALL,
      new Function<PsiElement, String>() {
        @Override
        public String fun(PsiElement element) {
          return isInterface
                 ? DaemonBundle.message("method.is.implemented.too.many")
                 : DaemonBundle.message("method.is.overridden.too.many");
        }
      },
      new GutterIconNavigationHandler<PsiElement>() {
        @Override
        public void navigate(MouseEvent e, PsiElement elt) {
          PsiElementListNavigator.openTargets(
            e, LogtalkResolveUtil.getComponentNames(filteredSubItems).toArray(new NavigatablePsiElement[filteredSubItems.size()]),
            isInterface ?
            DaemonBundle.message("navigation.title.implementation.method", namedComponent.getName(), filteredSubItems.size())
                        :
            DaemonBundle.message("navigation.title.overrider.method", namedComponent.getName(), filteredSubItems.size()),
            "Implementations of " + namedComponent.getName(),
            new DefaultPsiElementCellRenderer()
          );
        }
      },
      GutterIconRenderer.Alignment.RIGHT
    );
  }

  @Nullable
  private static LineMarkerInfo createImplementationMarker(final LogtalkClass componentWithDeclarationList,
                                                           final List<LogtalkClass> items) {
    final LogtalkComponentName componentName = componentWithDeclarationList.getComponentName();
    if (componentName == null) {
      return null;
    }
    final PsiElement element = componentName.getIdentifier().getFirstChild();
    return new LineMarkerInfo<>(
      element,
      element.getTextRange(),
      componentWithDeclarationList instanceof LogtalkInterfaceDeclaration
      ? AllIcons.Gutter.ImplementedMethod
      : AllIcons.Gutter.OverridenMethod,
      Pass.UPDATE_ALL,
      item -> DaemonBundle.message("method.is.implemented.too.many"),
      new GutterIconNavigationHandler<PsiElement>() {
        @Override
        public void navigate(MouseEvent e, PsiElement elt) {
          PsiElementListNavigator.openTargets(
            e, LogtalkResolveUtil.getComponentNames(items).toArray(new NavigatablePsiElement[items.size()]),
            DaemonBundle.message("navigation.title.subclass", componentWithDeclarationList.getName(), items.size()),
            "Subclasses of " + componentWithDeclarationList.getName(),
            new DefaultPsiElementCellRenderer()
          );
        }
      },
      GutterIconRenderer.Alignment.RIGHT
    );
  }
}
