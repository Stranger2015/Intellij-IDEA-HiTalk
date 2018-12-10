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

import com.intellij.lang.ASTNode;
import com.intellij.plugins.logtalk.lang.psi.*;
import com.intellij.plugins.logtalk.util.LogtalkResolveUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public abstract class AbstractLogtalkTypeDefImpl extends AbstractLogtalkPsiClass implements LogtalkTypedefDeclaration {
  public AbstractLogtalkTypeDefImpl(@NotNull ASTNode node) {
    super(node);
  }

  public LogtalkClassResolveResult getTargetClass() {
    return getTargetClass(new LogtalkGenericSpecialization());
  }

  public LogtalkClassResolveResult getTargetClass(LogtalkGenericSpecialization specialization) {
    final LogtalkTypeOrAnonymous haxeTypeOrAnonymous = getFirstItem(getTypeOrAnonymousList());
    if (haxeTypeOrAnonymous == null) {
      // cause parse error
      return LogtalkClassResolveResult.create(null);
    }
    if (haxeTypeOrAnonymous.getAnonymousType() != null) {
      return LogtalkClassResolveResult.create(haxeTypeOrAnonymous.getAnonymousType(), specialization);
    }
    return LogtalkResolveUtil.getLogtalkClassResolveResult(haxeTypeOrAnonymous.getType(), specialization);
  }

  @NotNull
  @Override
  public List<LogtalkType> getLogtalkExtendsList() {
    final LogtalkClass targetLogtalkClass = getTargetClass().getLogtalkClass();
    if (targetLogtalkClass != null) {
      return targetLogtalkClass.getLogtalkExtendsList();
    }
    return super.getLogtalkExtendsList();
  }

  @NotNull
  @Override
  public List<LogtalkType> getLogtalkImplementsList() {
    final LogtalkClass targetLogtalkClass = getTargetClass().getLogtalkClass();
    if (targetLogtalkClass != null) {
      return targetLogtalkClass.getLogtalkImplementsList();
    }
    return super.getLogtalkImplementsList();
  }

  @Override
  public boolean isInterface() {
    final LogtalkClass targetLogtalkClass = getTargetClass().getLogtalkClass();
    if (targetLogtalkClass != null) {
      return targetLogtalkClass.isInterface();
    }
    return super.isInterface();
  }

  @NotNull
  @Override
  public List<LogtalkMethod> getLogtalkMethods() {
    final LogtalkClass targetLogtalkClass = getTargetClass().getLogtalkClass();
    if (targetLogtalkClass != null) {
      return targetLogtalkClass.getLogtalkMethods();
    }
    return super.getLogtalkMethods();
  }

  @NotNull
  @Override
  public List<LogtalkNamedComponent> getLogtalkFields() {
    final LogtalkClass targetLogtalkClass = getTargetClass().getLogtalkClass();
    if (targetLogtalkClass != null) {
      return targetLogtalkClass.getLogtalkFields();
    }
    return super.getLogtalkFields();
  }

  @Override
  public LogtalkNamedComponent findLogtalkFieldByName(@NotNull String name) {
    final LogtalkClass targetLogtalkClass = getTargetClass().getLogtalkClass();
    if (targetLogtalkClass != null) {
      return targetLogtalkClass.findLogtalkFieldByName(name);
    }
    return super.findLogtalkFieldByName(name);
  }

  @Override
  public LogtalkNamedComponent findLogtalkMethodByName(@NotNull String name) {
    final LogtalkClass targetLogtalkClass = getTargetClass().getLogtalkClass();
    if (targetLogtalkClass != null) {
      return targetLogtalkClass.findLogtalkMethodByName(name);
    }
    return super.findLogtalkMethodByName(name);
  }
}
