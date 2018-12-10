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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkFile;
import com.intellij.plugins.logtalk.lang.psi.LogtalkTypePsiMixin;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiImplUtil;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ebishton on 10/9/14.
 */
public class LogtalkTypePsiMixinImpl extends LogtalkPsiCompositeElementImpl implements LogtalkTypePsiMixin {

  private static Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.lang.psi.impl.LogtalkTypePsiMixin");
  {
    LOG.setLevel(Level.DEBUG);
  }

  public LogtalkTypePsiMixinImpl(ASTNode node) {
    super(node);
  }


  @Nullable
  @Override
  public PsiType getPsiType() {
    return (this instanceof LogtalkType) ? new LogtalkPsiTypeAdapter((LogtalkType)this) : null;
  }

  @Override
  public boolean hasTypeParameters() {
    return getTypeParameters().length != 0;
  }

  @Nullable
  @Override
  public PsiTypeParameterList getTypeParameterList() {
    return (LogtalkTypeParam) findChildByType(LogtalkTokenTypes.TYPE_PARAM);
  }

  @NotNull
  @Override
  public PsiTypeParameter[] getTypeParameters() {
    return PsiImplUtil.getTypeParameters(this);
  }

  @Nullable
  @Override
  public PsiClass getContainingClass() {
    PsiElement parent = getParent();
    while (parent != null) {
      if (parent instanceof LogtalkFile) {
        // If we get to the file node, we've gone too far.
        return null;
      }
      if (parent instanceof LogtalkClass) {
        return (LogtalkClass)parent;
      }
      parent = parent.getParent();
    }
    return null;
  }
}
