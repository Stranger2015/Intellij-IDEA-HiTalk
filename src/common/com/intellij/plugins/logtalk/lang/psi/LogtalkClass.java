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
package com.intellij.plugins.logtalk.lang.psi;

import com.intellij.plugins.logtalk.model.LogtalkClassModel;
import com.intellij.plugins.logtalk.model.LogtalkModelTarget;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public interface LogtalkClass extends LogtalkComponent, PsiClass, LogtalkModelTarget, LogtalkMetaContainerElement {
  LogtalkClass[] EMPTY_ARRAY = new LogtalkClass[0];

  @NotNull
  @NonNls
  String getQualifiedName();

  LogtalkClassModel getModel();

  @NotNull
  List<LogtalkType> getLogtalkExtendsList();

  @NotNull
  List<LogtalkType> getLogtalkImplementsList();

  boolean isAbstract();

  boolean isExtern();

  boolean isInterface();

  @NotNull
  List<LogtalkMethod> getLogtalkMethods();

  @NotNull
  List<LogtalkNamedComponent> getLogtalkFields();

  @NotNull
  List<LogtalkVarDeclaration> getVarDeclarations();

  @Nullable
  LogtalkNamedComponent findLogtalkFieldByName(@NotNull final String name);

  @Nullable
  LogtalkNamedComponent findLogtalkMethodByName(@NotNull final String name);

  boolean isGeneric();

  @Nullable
  LogtalkGenericParam getGenericParam();

  @Nullable
  LogtalkNamedComponent findArrayAccessGetter();
}
