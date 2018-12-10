/*
 * Copyright 2018 Ilya Malanin
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
package com.intellij.plugins.logtalk.ide.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.logtalk.model.FullyQualifiedInfo;
import com.intellij.plugins.logtalk.model.LogtalkExposableModel;
import com.intellij.plugins.logtalk.model.LogtalkModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LogtalkLookupElementFactory {
  public static LookupElementBuilder create(@NotNull LogtalkModel model) {
    return create(model, null);
  }

  @Nullable
  public static LookupElementBuilder create(@NotNull LogtalkModel model, @Nullable String alias) {
    PsiElement basePsi = model.getBasePsi();
    LogtalkNamedComponent namedComponent = getNamedComponent(basePsi);

    if (namedComponent == null) {
      return null;
    }

    String name = StringUtil.defaultIfEmpty(alias, model.getName());
    String presentableText = null;
    String tailText = getParentPath(model);
    Icon icon = null;

    ItemPresentation presentation = namedComponent.getPresentation();
    if (presentation != null) {
      icon = presentation.getIcon(false);
      presentableText = presentation.getPresentableText();
    }

    LookupElementBuilder lookupElement = LookupElementBuilder.create(basePsi, name);

    if (presentableText != null) {
      if (alias != null && presentableText.startsWith(model.getName())) {
        presentableText = presentableText.replace(model.getName(), alias);
      }
      lookupElement = lookupElement.withPresentableText(presentableText);
    }

    if (icon != null) lookupElement = lookupElement.withIcon(icon);

    if (tailText != null) {
      if (alias != null) {
        tailText = LogtalkBundle.message("logtalk.lookup.alias", tailText + "." + model.getName());
      }
      tailText = " " + tailText;
      lookupElement = lookupElement.withTailText(tailText, true);
    }

    return lookupElement;
  }

  @Nullable
  private static LogtalkNamedComponent getNamedComponent(PsiElement element) {
    return element instanceof LogtalkNamedComponent
           ? (LogtalkNamedComponent)element
           : PsiTreeUtil.findChildOfType(element, LogtalkNamedComponent.class);
  }

  @Nullable
  private static String getParentPath(@NotNull LogtalkModel model) {
    final LogtalkExposableModel parent = model.getExhibitor();
    if (parent != null) {
      final FullyQualifiedInfo qualifiedInfo = parent.getQualifiedInfo();
      if (qualifiedInfo != null) {
        return qualifiedInfo.getPresentableText();
      }
    }

    return null;
  }
}
