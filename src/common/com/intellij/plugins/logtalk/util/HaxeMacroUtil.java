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

import com.intellij.openapi.util.Key;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponentName;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.plugins.logtalk.lang.psi.LogtalkNamedComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkMacroUtil {
  public static Set<LogtalkComponentName> findVariables(@Nullable PsiElement at) {
    if (at == null) {
      return Collections.emptySet();
    }
    final Set<LogtalkComponentName> result = new THashSet<LogtalkComponentName>();
    PsiTreeUtil.treeWalkUp(new PsiScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement element, ResolveState state) {
        if (element instanceof LogtalkNamedComponent) {
          final LogtalkNamedComponent haxeNamedComponent = (LogtalkNamedComponent)element;
          if (haxeNamedComponent.getComponentName() != null && LogtalkComponentType
            .isVariable(LogtalkComponentType.typeOf(haxeNamedComponent))) {
            result.add(haxeNamedComponent.getComponentName());
          }
        }
        return true;
      }

      @Override
      public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
      }

      @Override
      public void handleEvent(Event event, @Nullable Object associated) {
      }
    }, at, null, ResolveState.initial());
    return result;
  }
}
