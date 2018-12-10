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
package com.intellij.plugins.logtalk.model.type;

import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.model.LogtalkClassModel;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.model.LogtalkClassModel;
import com.intellij.plugins.logtalk.util.LogtalkResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class LogtalkClassReference {
  final public String name;
  @NotNull final public PsiElement elementContext;
  final public LogtalkClassModel clazz;

  public LogtalkClassReference(String name, @NotNull PsiElement elementContext) {
    this.name = name;
    this.elementContext = elementContext;
    this.clazz = null;
  }

  public LogtalkClassReference(LogtalkClassModel clazz, @NotNull PsiElement elementContext) {
    this.name = clazz.getName();
    this.elementContext = elementContext;
    this.clazz = clazz;
  }

  public LogtalkClass getLogtalkClass() {
    if (this.clazz != null) return this.clazz.getPsi();
    LogtalkClass clazz = LogtalkResolveUtil.findClassByQName(name, elementContext);
    if (clazz == null) {
      clazz = LogtalkResolveUtil.tryResolveClassByQName(elementContext);
      // Null is a legitimate answer in the case of Dynamic or Unknown.
      // (Plus, this should be logging, not dumping to stderr.)
      //if (clazz == null) {
      //  System.err.println("Not found '" + name + "' : " + elementContext + " : " + elementContext.getText());
      //}
    }
    return clazz;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public boolean equals(Object obj) {
    // @TODO: This should check fqNames
    if (obj instanceof LogtalkClassReference) return getName().equals(((LogtalkClassReference)obj).getName());
    return false;
  }
}
