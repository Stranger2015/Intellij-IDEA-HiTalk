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
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.haxe.lang.psi.LogtalkRegularExpression;
import com.intellij.plugins.haxe.lang.psi.LogtalkVarDeclaration;
import com.intellij.plugins.haxe.lang.psi.LogtalkVarInit;
import com.intellij.plugins.haxe.util.LogtalkElementGenerator;
import com.intellij.plugins.logtalk.lang.psi.LogtalkRegularExpression;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.lang.regexp.DefaultRegExpPropertiesProvider;
import org.intellij.lang.regexp.psi.RegExpChar;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.intellij.lang.regexp.psi.RegExpNamedGroupRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkRegularExpressionImpl extends LogtalkReferenceImpl implements LogtalkRegularExpression {
  private final DefaultRegExpPropertiesProvider myPropertiesProvider;

  public LogtalkRegularExpressionImpl(@NotNull ASTNode node) {
    super(node);
    myPropertiesProvider = DefaultRegExpPropertiesProvider.getInstance();
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public PsiLanguageInjectionHost updateText(@NotNull String text) {
    ASTNode node = getNode();
    ASTNode parent = node.getTreeParent();
    final LogtalkVarDeclaration varDeclarationPart = LogtalkElementGenerator.createVarDeclaration(getProject(), "a=" + text);
    final LogtalkVarInit varInit = varDeclarationPart.getVarInit();
    final ASTNode outerNode = varInit == null ? null : varInit.getNode();
    assert outerNode != null;
    parent.replaceChild(node, outerNode);

    return (PsiLanguageInjectionHost)outerNode.getPsi();
  }

  @NotNull
  @Override
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new LiteralTextEscaper<LogtalkRegularExpression>(this) {
      @Override
      public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
        outChars.append(myHost.getText(), rangeInsideHost.getStartOffset(), rangeInsideHost.getEndOffset());
        return true;
      }

      @Override
      public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
        int offset = offsetInDecoded + rangeInsideHost.getStartOffset();
        if (offset < rangeInsideHost.getStartOffset()) offset = rangeInsideHost.getStartOffset();
        if (offset > rangeInsideHost.getEndOffset()) offset = rangeInsideHost.getEndOffset();
        return offset;
      }

      @Override
      public boolean isOneLine() {
        return true;
      }
    };
  }

  @Override
  public boolean characterNeedsEscaping(char c) {
    return false;
  }

  @Override
  public boolean supportsPerl5EmbeddedComments() {
    return false;
  }

  @Override
  public boolean supportsPossessiveQuantifiers() {
    return false;
  }

  @Override
  public boolean supportsPythonConditionalRefs() {
    return false;
  }

  @Override
  public boolean supportsNamedGroupSyntax(RegExpGroup group) {
    return false;
  }

  @Override
  public boolean supportsNamedGroupRefSyntax(RegExpNamedGroupRef ref) {
    return false;
  }

  @Override
  public boolean supportsExtendedHexCharacter(RegExpChar aChar) {
    return false;
  }

  @Override
  public boolean isValidCategory(@NotNull String category) {
    return myPropertiesProvider.isValidCategory(category);
  }

  @NotNull
  @Override
  public String[][] getAllKnownProperties() {
    return myPropertiesProvider.getAllKnownProperties();
  }

  @Nullable
  @Override
  public String getPropertyDescription(@Nullable String name) {
    return myPropertiesProvider.getPropertyDescription(name);
  }

  @NotNull
  @Override
  public String[][] getKnownCharacterClasses() {
    return myPropertiesProvider.getKnownCharacterClasses();
  }

  // LogtalkLiteralExpression
  //@Nullable
  //@Override
  //public LogtalkBlockStatement getBlockStatement() {
  //  return LogtalkStatementUtils.getBlockStatement(this);
  //}

}
