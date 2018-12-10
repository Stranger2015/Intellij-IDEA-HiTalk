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

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.MemberChooserObject;
import com.intellij.codeInsight.generation.PsiElementMemberChooserObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkNamedElementNode extends PsiElementMemberChooserObject implements ClassMember {
  public LogtalkNamedElementNode(final LogtalkNamedComponent haxeNamedComponent) {
    super(haxeNamedComponent, buildPresentationText(haxeNamedComponent), haxeNamedComponent.getIcon(Iconable.ICON_FLAG_VISIBILITY));
  }

  @Nullable
  private static String buildPresentationText(LogtalkNamedComponent haxeNamedComponent) {
    final ItemPresentation presentation = haxeNamedComponent.getPresentation();
    if (presentation == null) {
      return haxeNamedComponent.getName();
    }
    final StringBuilder result = new StringBuilder();
    if (haxeNamedComponent instanceof LogtalkClass) {
      result.append(haxeNamedComponent.getName());
      final String location = presentation.getLocationString();
      if (location != null && !location.isEmpty()) {
        result.append(" ").append(location);
      }
    }
    else {
      result.append(presentation.getPresentableText());
    }
    return result.toString();
  }

  @Nullable
  @Override
  public MemberChooserObject getParentNodeDelegate() {
    final LogtalkNamedComponent result = PsiTreeUtil.getParentOfType(getPsiElement(), LogtalkNamedComponent.class);
    return result == null ? null : new LogtalkNamedElementNode(result);
  }
}
