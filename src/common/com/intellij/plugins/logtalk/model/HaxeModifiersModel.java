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
package com.intellij.plugins.logtalk.model;

import com.intellij.plugins.haxe.lang.psi.LogtalkCustomMeta;
import com.intellij.plugins.haxe.lang.psi.LogtalkDeclarationAttribute;
import com.intellij.plugins.haxe.lang.psi.LogtalkFinalMeta;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class LogtalkModifiersModel {
  private PsiElement baseElement;

  public LogtalkModifiersModel(PsiElement baseElement) {
    this.baseElement = baseElement;
  }

  public boolean hasModifier(LogtalkModifierType modifier) {
    return getModifierPsi(modifier) != null;
  }

  public boolean hasAnyModifier(LogtalkModifierType... modifiers) {
    for (LogtalkModifierType modifier : modifiers) if (hasModifier(modifier)) return true;
    return false;
  }

  public boolean hasAllModifiers(LogtalkModifierType... modifiers) {
    for (LogtalkModifierType modifier : modifiers) if (!hasModifier(modifier)) return false;
    return true;
  }

  public PsiElement getModifierPsi(LogtalkModifierType modifier) {
    PsiElement result = UsefulPsiTreeUtil.getChildWithText(baseElement, LogtalkDeclarationAttribute.class, modifier.s);
    if (result == null) result = UsefulPsiTreeUtil.getChildWithText(baseElement, LogtalkFinalMeta.class, modifier.s);
    if (result == null) result = UsefulPsiTreeUtil.getChildWithText(baseElement, LogtalkCustomMeta.class, modifier.s);
    return result;
  }

  public PsiElement getModifierPsiOrBase(LogtalkModifierType modifier) {
    PsiElement psi = getModifierPsi(modifier);
    if (psi == null) psi = this.baseElement;
    return psi;
  }

  public void replaceVisibility(LogtalkModifierType modifier) {
    PsiElement psi = getVisibilityPsi();
    if (psi != null) {
      getDocument().replaceElementText(psi, modifier.getStringWithSpace(), StripSpaces.AFTER);
    } else {
      addModifier(modifier);
    }
  }

  public void removeModifier(LogtalkModifierType modifier) {
    PsiElement psi = getModifierPsi(modifier);
    if (psi != null) {
      getDocument().replaceElementText(psi, "", StripSpaces.AFTER);
    }
  }

  public void sortModifiers() {
    // @TODO implement this!
  }

  private LogtalkDocumentModel _document = null;
  @NotNull
  public LogtalkDocumentModel getDocument() {
    if (_document == null) _document = new LogtalkDocumentModel(baseElement);
    return _document;
  }

  public void addModifier(LogtalkModifierType modifier) {
    getDocument().addTextBeforeElement(baseElement, modifier.getStringWithSpace());
  }

  public PsiElement getVisibilityPsi() {
    PsiElement element = getModifierPsi(LogtalkModifierType.PUBLIC);
    if (element == null) element = getModifierPsi(LogtalkModifierType.PRIVATE);
    return element;
  }

  public LogtalkModifierType getVisibility() {
    if (getModifierPsi(LogtalkModifierType.PUBLIC) != null) return LogtalkModifierType.PUBLIC;
    if (getModifierPsi(LogtalkModifierType.PRIVATE) != null) return LogtalkModifierType.PRIVATE;
    return LogtalkModifierType.EMPTY;
  }
}
