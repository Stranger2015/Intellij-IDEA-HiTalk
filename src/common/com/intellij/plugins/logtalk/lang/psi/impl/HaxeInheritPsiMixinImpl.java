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
import com.intellij.plugins.haxe.lang.psi.LogtalkExtendsDeclaration;
import com.intellij.plugins.haxe.lang.psi.LogtalkImplementsDeclaration;
import com.intellij.plugins.haxe.lang.psi.LogtalkInheritPsiMixin;
import com.intellij.plugins.haxe.lang.psi.LogtalkType;
import com.intellij.plugins.logtalk.lang.psi.LogtalkInheritPsiMixin;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * LogtalkInherit is analogous to PsiJavaCodeReferenceElement
 * Created by ebishton on 10/8/14.
 */
public class LogtalkInheritPsiMixinImpl extends LogtalkPsiCompositeElementImpl implements LogtalkInheritPsiMixin {

  private static final Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.lang.psi.impl.LogtalkInheritPsiMixinImpl");

  //static {
  //  // Turn on all local messages.
  //  LOG.setLevel(Level.DEBUG);
  //}

  /**
   * The empty array of PSI Java code references which can be reused to avoid unnecessary allocations.
   */
  LogtalkInheritPsiMixin[] EMPTY_ARRAY = new LogtalkInheritPsiMixinImpl[0];

  ArrayFactory<LogtalkInheritPsiMixin> ARRAY_FACTORY = new ArrayFactory<LogtalkInheritPsiMixin>() {
    @NotNull
    @Override
    public LogtalkInheritPsiMixin[] create(int count) {
      return count == 0 ? EMPTY_ARRAY : new LogtalkInheritPsiMixin[count];
    }
  };


  public LogtalkInheritPsiMixinImpl(ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public PsiClassType[] getReferencedTypes() {
    LOG.debug("getReferencedTypes");
    PsiJavaCodeReferenceElement[] refs = getReferenceElements();
    PsiElementFactory factory = JavaPsiFacade.getInstance(getProject()).getElementFactory();
    PsiClassType[] types = new PsiClassType[refs.length];
    for (int i = 0; i < types.length; i++) {
      types[i] = factory.createType(refs[i]);
    }

    return types;
  }

  @NotNull
  @Override
  public PsiJavaCodeReferenceElement[] getReferenceElements() {
    LOG.debug("getReferenceElements");
    List<LogtalkType> typeList = getTypeList();
    PsiJavaCodeReferenceElement[] refList = new PsiJavaCodeReferenceElement[typeList.size()];
    for (int i = 0; i < typeList.size(); ++i) {
      refList[i] = typeList.get(i).getReferenceExpression();
    }
    return refList;
  }

  @Override
  public Role getRole() {
    if (this instanceof LogtalkExtendsDeclaration) {
      return Role.EXTENDS_LIST;
    } else if (this instanceof LogtalkImplementsDeclaration) {
      return Role.IMPLEMENTS_LIST;
    }
    LOG.assertTrue(false, "Unrecognized/unexpected subclass type.");
    return null;
  }

  // Pushed down from LogtalkInherit
  // Implementation is the same as the sub-class implementations that are generated
  // from the BNF.  If you have to change this code, then there may be complications,
  // because their implementations will be called.
  @NotNull
  public List<LogtalkType> getTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LogtalkType.class);
  }

}
