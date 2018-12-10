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
package com.intellij.plugins.logtalk.ide.completion;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClassResolveResult;
import com.intellij.plugins.logtalk.lang.psi.LogtalkGenericSpecialization;
import com.intellij.plugins.logtalk.lang.psi.LogtalkReference;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LogtalkIdentifier implements LogtalkReference {
  @NotNull
  @Override
  public LogtalkClassResolveResult resolveLogtalkClass() {
    return null;
  }

  @Nullable
  @Override
  public PsiType getPsiType() {
    return null;
  }

  @Nullable
  @Override
  public PsiElement resolveToComponentName() {
    return null;
  }

  @Override
  public boolean resolveIsStaticExtension() {
    return false;
  }

  @Nullable
  @Override
  public LogtalkGenericSpecialization getSpecialization() {
    return null;
  }

  @Nullable
  @Override
  public LogtalkBlockStatement getBlockStatement() {
    return null;
  }

  @Nullable
  @Override
  public PsiElement getReferenceNameElement() {
    return null;
  }

  @Nullable
  @Override
  public PsiReferenceParameterList getParameterList() {
    return null;
  }

  @NotNull
  @Override
  public PsiType[] getTypeParameters() {
    return new PsiType[0];
  }

  @Override
  public boolean isQualified() {
    return false;
  }

  @Override
  public String getQualifiedName() {
    return null;
  }

  @NotNull
  @Override
  public Project getProject() throws PsiInvalidElementAccessException {
    return null;
  }

  @NotNull
  @Override
  public Language getLanguage() {
    return null;
  }

  @Override
  public PsiManager getManager() {
    return null;
  }

  @NotNull
  @Override
  public PsiElement[] getChildren() {
    return new PsiElement[0];
  }

  @Override
  public PsiElement getParent() {
    return null;
  }

  @Override
  public PsiElement getFirstChild() {
    return null;
  }

  @Override
  public PsiElement getLastChild() {
    return null;
  }

  @Override
  public PsiElement getNextSibling() {
    return null;
  }

  @Override
  public PsiElement getPrevSibling() {
    return null;
  }

  @Override
  public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
    return null;
  }

  @Override
  public TextRange getTextRange() {
    return null;
  }

  @Override
  public int getStartOffsetInParent() {
    return 0;
  }

  @Override
  public int getTextLength() {
    return 0;
  }

  @Nullable
  @Override
  public PsiElement findElementAt(int i) {
    return null;
  }

  @Nullable
  @Override
  public PsiReference findReferenceAt(int i) {
    return null;
  }

  @Override
  public int getTextOffset() {
    return 0;
  }

  @Override
  public String getText() {
    return null;
  }

  @NotNull
  @Override
  public char[] textToCharArray() {
    return new char[0];
  }

  @Override
  public PsiElement getNavigationElement() {
    return null;
  }

  @Override
  public PsiElement getOriginalElement() {
    return null;
  }

  @Override
  public boolean textMatches(@NotNull CharSequence sequence) {
    return false;
  }

  @Override
  public boolean textMatches(@NotNull PsiElement element) {
    return false;
  }

  @Override
  public boolean textContains(char c) {
    return false;
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {

  }

  @Override
  public void acceptChildren(@NotNull PsiElementVisitor visitor) {

  }

  @Override
  public PsiElement copy() {
    return null;
  }

  @Override
  public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
    return null;
  }

  @Override
  public PsiElement addBefore(@NotNull PsiElement element, @Nullable PsiElement element1) throws IncorrectOperationException {
    return null;
  }

  @Override
  public PsiElement addAfter(@NotNull PsiElement element, @Nullable PsiElement element1) throws IncorrectOperationException {
    return null;
  }

  /**
   * @param element
   * @deprecated
   */
  @Override
  public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException {

  }

  @Override
  public PsiElement addRange(PsiElement element, PsiElement element1) throws IncorrectOperationException {
    return null;
  }

  @Override
  public PsiElement addRangeBefore(@NotNull PsiElement element, @NotNull PsiElement element1, PsiElement element2)
    throws IncorrectOperationException {
    return null;
  }

  @Override
  public PsiElement addRangeAfter(PsiElement element, PsiElement element1, PsiElement element2) throws IncorrectOperationException {
    return null;
  }

  @Override
  public void delete() throws IncorrectOperationException {

  }

  /**
   * @deprecated
   */
  @Override
  public void checkDelete() throws IncorrectOperationException {

  }

  @Override
  public void deleteChildRange(PsiElement element, PsiElement element1) throws IncorrectOperationException {

  }

  @Override
  public PsiElement replace(@NotNull PsiElement element) throws IncorrectOperationException {
    return null;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public boolean isWritable() {
    return false;
  }

  @Nullable
  @Override
  public PsiReference getReference() {
    return null;
  }

  @NotNull
  @Override
  public PsiReference[] getReferences() {
    return new PsiReference[0];
  }

  @Nullable
  @Override
  public <T> T getCopyableUserData(Key<T> key) {
    return null;
  }

  @Override
  public <T> void putCopyableUserData(Key<T> key, @Nullable T t) {

  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     @Nullable PsiElement element,
                                     @NotNull PsiElement element1) {
    return false;
  }

  @Nullable
  @Override
  public PsiElement getContext() {
    return null;
  }

  @Override
  public boolean isPhysical() {
    return false;
  }

  @NotNull
  @Override
  public GlobalSearchScope getResolveScope() {
    return null;
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
    return null;
  }

  @Override
  public ASTNode getNode() {
    return null;
  }

  @Override
  public boolean isEquivalentTo(PsiElement element) {
    return false;
  }

  @Override
  public Icon getIcon(int i) {
    return null;
  }

  @Nullable
  @Override
  public <T> T getUserData(@NotNull Key<T> key) {
    return null;
  }

  @Override
  public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

  }

  @Override
  public void processVariants(@NotNull PsiScopeProcessor processor) {

  }

  @NotNull
  @Override
  public JavaResolveResult advancedResolve(boolean b) {
    return null;
  }

  @NotNull
  @Override
  public JavaResolveResult[] multiResolve(boolean b) {
    return new JavaResolveResult[0];
  }

  @Nullable
  @Override
  public PsiElement getQualifier() {
    return null;
  }

  @Nullable
  @Override
  public String getReferenceName() {
    return null;
  }

  @NotNull
  @Override
  public PsiElement getElement() {
    return null;
  }

  @NotNull
  @Override
  public TextRange getRangeInElement() {
    return null;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return null;
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return null;
  }

  @Override
  public PsiElement handleElementRename(String s) throws IncorrectOperationException {
    return null;
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return null;
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return false;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @Override
  public boolean isSoft() {
    return false;
  }
}
