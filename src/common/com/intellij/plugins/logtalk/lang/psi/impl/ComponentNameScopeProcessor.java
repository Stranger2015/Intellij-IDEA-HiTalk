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
package com.intellij.plugins.logtalk.lang.psi.impl;

import com.intellij.openapi.util.Key;
import com.intellij.plugins.logtalk.lang.psi.LogtalkNamedComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public class ComponentNameScopeProcessor implements PsiScopeProcessor {
  private final Set<LogtalkComponentName> result;

  public ComponentNameScopeProcessor(Set<LogtalkComponentName> result) {
    this.result = result;
  }

  @Override
  public boolean execute(@NotNull PsiElement element, ResolveState state) {
    if (element instanceof LogtalkNamedComponent) {
      final LogtalkNamedComponent haxeNamedComponent = (LogtalkNamedComponent)element;
      if (haxeNamedComponent.getComponentName() != null) {
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
}
