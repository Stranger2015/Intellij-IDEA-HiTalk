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
package com.intellij.plugins.logtalk.ide.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.logtalk.lang.lexer.LogtalkLexer;
import com.intellij.plugins.logtalk.lang.lexer.LogtalkTokenTypeSets;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.plugins.logtalk.lang.lexer.LogtalkTokenTypeSets.*;


public class LogtalkSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();
  private Project myProject;

  public LogtalkSyntaxHighlighter(Project project) {
    myProject = project;
  }

  static {
    fillMap(ATTRIBUTES, KEYWORDS, LogtalkSyntaxHighlighterColors.KEYWORD);
    fillMap(ATTRIBUTES, OPERATORS, LogtalkSyntaxHighlighterColors.OPERATION_SIGN);

    ATTRIBUTES.put(LITINT, LogtalkSyntaxHighlighterColors.NUMBER);
    ATTRIBUTES.put(LITHEX, LogtalkSyntaxHighlighterColors.NUMBER);
    ATTRIBUTES.put(LITOCT, LogtalkSyntaxHighlighterColors.NUMBER);
    ATTRIBUTES.put(KFALSE, LogtalkSyntaxHighlighterColors.NUMBER);
    ATTRIBUTES.put(KTRUE, LogtalkSyntaxHighlighterColors.NUMBER);
    ATTRIBUTES.put(LITFLOAT, LogtalkSyntaxHighlighterColors.NUMBER);

    ATTRIBUTES.put(OPEN_QUOTE, LogtalkSyntaxHighlighterColors.STRING);
    ATTRIBUTES.put(CLOSING_QUOTE, LogtalkSyntaxHighlighterColors.STRING);
    ATTRIBUTES.put(REGULAR_STRING_PART, LogtalkSyntaxHighlighterColors.STRING);

    ATTRIBUTES.put(PLPAREN, LogtalkSyntaxHighlighterColors.PARENTHS);
    ATTRIBUTES.put(PRPAREN, LogtalkSyntaxHighlighterColors.PARENTHS);

    ATTRIBUTES.put(PLCURLY, LogtalkSyntaxHighlighterColors.BRACES);
    ATTRIBUTES.put(PRCURLY, LogtalkSyntaxHighlighterColors.BRACES);

    ATTRIBUTES.put(PLBRACK, LogtalkSyntaxHighlighterColors.BRACKETS);
    ATTRIBUTES.put(PRBRACK, LogtalkSyntaxHighlighterColors.BRACKETS);

    ATTRIBUTES.put(OCOMMA, LogtalkSyntaxHighlighterColors.COMMA);
    ATTRIBUTES.put(ODOT, LogtalkSyntaxHighlighterColors.DOT);
    ATTRIBUTES.put(OSEMI, LogtalkSyntaxHighlighterColors.SEMICOLON);

    ATTRIBUTES.put(LogtalkTokenTypeSets.MML_COMMENT, LogtalkSyntaxHighlighterColors.BLOCK_COMMENT);
    ATTRIBUTES.put(MSL_COMMENT, LogtalkSyntaxHighlighterColors.LINE_COMMENT);
    ATTRIBUTES.put(DOC_COMMENT, LogtalkSyntaxHighlighterColors.DOC_COMMENT);

    fillMap(ATTRIBUTES, BAD_TOKENS, LogtalkSyntaxHighlighterColors.BAD_CHARACTER);
    fillMap(ATTRIBUTES, CONDITIONALLY_NOT_COMPILED, LogtalkSyntaxHighlighterColors.CONDITIONALLY_NOT_COMPILED);
  }

  @NotNull
  public Lexer getHighlightingLexer() {
    return new LogtalkLexer(myProject);
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(ATTRIBUTES.get(tokenType));
  }
}

