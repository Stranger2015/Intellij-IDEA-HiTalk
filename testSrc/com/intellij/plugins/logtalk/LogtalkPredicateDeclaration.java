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
package com.intellij.plugins.logtalk;



public class LogtalkPredicateDeclaration implements PsiElement {
  private static final PsiElement[] EMPTY_PSI_ELEM_ARRAY = new PsiElement[0];
  private Project prj;
  private PsiManager psiManager;
  private PsiElement parent;

  @NotNull
  @Override
  public Project getProject() throws PsiInvalidElementAccessException {
    return prj;
  }

  @NotNull
  @Override
  public Language getLanguage() {
    return Language.findInstance(LogtalkLanguage.class);
  }

  @Override
  public PsiManager getManager() {
    return psiManager;
  }

  @NotNull
  @Override
  public PsiElement[] getChildren() {
    return EMPTY_PSI_ELEM_ARRAY;
  }

  @Override
  public PsiElement getParent() {
    return parent;
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

  @java.lang.Override
  public boolean textMatches(java.lang.CharSequence sequence) {
    return false;
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

  @java.lang.Override
  public void accept(com.intellij.psi.PsiElementVisitor visitor) {

  }

  @java.lang.Override
  public void acceptChildren(com.intellij.psi.PsiElementVisitor visitor) {

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

  @java.lang.Override
  public boolean processDeclarations(com.intellij.psi.scope.PsiScopeProcessor processor,
                                     com.intellij.psi.ResolveState state,
                                     com.intellij.psi.PsiElement element,
                                     com.intellij.psi.PsiElement element1) {
    return false;
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

  @java.lang.Override
  public java.lang.String toString() {
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
}
