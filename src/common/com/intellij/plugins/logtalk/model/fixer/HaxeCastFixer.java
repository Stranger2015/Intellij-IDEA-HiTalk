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
package com.intellij.plugins.logtalk.model.fixer;

import com.intellij.plugins.haxe.model.LogtalkDocumentModel;
import com.intellij.plugins.haxe.model.type.LogtalkCastUtil;
import com.intellij.plugins.haxe.model.type.SpecificTypeReference;
import com.intellij.plugins.logtalk.model.LogtalkDocumentModel;
import com.intellij.plugins.logtalk.model.type.LogtalkCastUtil;
import com.intellij.plugins.logtalk.model.type.SpecificTypeReference;
import com.intellij.psi.PsiElement;

public class LogtalkCastFixer extends LogtalkFixer {
  PsiElement element;
  SpecificTypeReference from;
  SpecificTypeReference to;

  public LogtalkCastFixer(
    PsiElement element,
    SpecificTypeReference from,
    SpecificTypeReference to
  ) {
    super("Add cast");
    this.element = element;
    this.from = from;
    this.to = to;
  }

  @Override
  public void run() {
    LogtalkDocumentModel.fromElement(element).replaceElementText(
      element,
      LogtalkCastUtil.getCastText(element, from, to)
    );
  }
}
