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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkModifierList;
import com.intellij.plugins.logtalk.lang.psi.LogtalkModifierListOwner;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiCompositeElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This class, ideally, must not derive from LogtalkModifierListOwner because every element cannot be prefixed with modifiers/annotations.
 * E.g. try/catch blocks, Interface body, Class body etc. do not have annotations attached to them.
 *
 * Unfortunately, it is observed that individual words read from the file are being validated whether they are methods, fields or have
 * annotations attached to them. This is causing .findMethodByName("void"), .findFieldByName("var"), .hasModifierByName("catch") etc
 * calls to be made and resulting in runtime errors / class-cast exceptions.
 *
 * To work around that, this 'is-a' relationship is introduced :(
 */

public class LogtalkPsiCompositeElementImpl extends ASTWrapperPsiElement implements LogtalkPsiCompositeElement, LogtalkModifierListOwner {
  public LogtalkPsiCompositeElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public IElementType getTokenType() {
    return getNode().getElementType();
  }

  public String getDebugName() {
    String name = getName();
    String text = getText();
    StringBuilder sb = new StringBuilder();
    if (null != name) {
      sb.append('\'');
      sb.append(name);
      sb.append('\'');
    }
    if (null != text) {
      if (null != name) {
        sb.append(' ');
      }
      sb.append('"');
      sb.append(text);
      sb.append('"');
    }
    return sb.toString();
  }

  public String toString() {
    String out = getTokenType().toString();
    if (!ApplicationManager.getApplication().isUnitTestMode()) {
      // Unit tests don't want the extra data.
      out += " " + getDebugName();
    }
    return out;
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place) {
    for (PsiElement element : getDeclarationElementToProcess(lastParent)) {
      if (!processor.execute(element, state)) {
        return false;
      }
    }
    return super.processDeclarations(processor, state, lastParent, place);
  }

  private List<PsiElement> getDeclarationElementToProcess(PsiElement lastParent) {
    final boolean isBlock = this instanceof LogtalkBlockStatement || this instanceof LogtalkSwitchCaseBlock;
    final PsiElement stopper = isBlock ? lastParent : null;
    final List<PsiElement> result = new ArrayList<PsiElement>();
    addVarDeclarations(result, PsiTreeUtil.getChildrenOfType(this, LogtalkVarDeclaration.class));
    addLocalVarDeclarations(result, UsefulPsiTreeUtil.getChildrenOfType(this, LogtalkLocalVarDeclarationList.class, stopper));

    addDeclarations(result, PsiTreeUtil.getChildrenOfType(this, LogtalkFunctionDeclarationWithAttributes.class));
    addDeclarations(result, UsefulPsiTreeUtil.getChildrenOfType(this, LogtalkLocalFunctionDeclaration.class, stopper));
    addDeclarations(result, PsiTreeUtil.getChildrenOfType(this, LogtalkClassDeclaration.class));
    addDeclarations(result, PsiTreeUtil.getChildrenOfType(this, LogtalkExternClassDeclaration.class));
    addDeclarations(result, PsiTreeUtil.getChildrenOfType(this, LogtalkEnumDeclaration.class));
    addDeclarations(result, PsiTreeUtil.getChildrenOfType(this, LogtalkInterfaceDeclaration.class));
    addDeclarations(result, PsiTreeUtil.getChildrenOfType(this, LogtalkTypedefDeclaration.class));

    final LogtalkParameterList parameterList = PsiTreeUtil.getChildOfType(this, LogtalkParameterList.class);
    if (parameterList != null) {
      result.addAll(parameterList.getParameterList());
    }
    final LogtalkOpenParameterList openParameterList = PsiTreeUtil.getChildOfType(this, LogtalkOpenParameterList.class);
    if (openParameterList != null) {
      result.add(openParameterList);
    }
    final LogtalkGenericParam tygenericParameParam = PsiTreeUtil.getChildOfType(this, LogtalkGenericParam.class);
    if (tygenericParameParam != null) {
      result.addAll(tygenericParameParam.getGenericListPartList());
    }

    if (this instanceof LogtalkForStatement && ((LogtalkForStatement)this).getIterable() != lastParent) {
      result.add(this);
    }

    if (this instanceof LogtalkCatchStatement) {
      final LogtalkParameter catchParameter = PsiTreeUtil.getChildOfType(this, LogtalkParameter.class);
      if(catchParameter != null) {
        result.add(catchParameter);
      }
    }
    return result;
  }

  private static void addLocalVarDeclarations(@NotNull List<PsiElement> result,
                                              @Nullable LogtalkLocalVarDeclarationList[] items) {
    if (items == null) {
      return;
    }

    Arrays.stream(items).forEach(list -> result.addAll(list.getLocalVarDeclarationList()));
  }

  private static void addVarDeclarations(@NotNull List<PsiElement> result, @Nullable LogtalkVarDeclaration[] items) {
    if (items == null) {
      return;
    }

    result.addAll(Arrays.asList(items));
  }

  private static void addDeclarations(@NotNull List<PsiElement> result, @Nullable PsiElement[] items) {
    if (items != null) {
      result.addAll(Arrays.asList(items));
    }
  }


  // LogtalkModifierListOwner implementations

  @Override
  public boolean hasModifierProperty(@PsiModifier.ModifierConstant @NonNls @NotNull String name) {
    LogtalkModifierList list = getModifierList();
    return null == list ? false : list.hasModifierProperty(name);
  }

  @Nullable
  @Override
  public LogtalkModifierList getModifierList() {
    LogtalkModifierList list = (LogtalkModifierList) this.findChildByType(LogtalkTokenTypes.MACRO_CLASS_LIST);
    return list;
  }

}
