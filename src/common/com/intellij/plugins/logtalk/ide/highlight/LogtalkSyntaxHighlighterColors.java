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

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * @author fedor.korotkov
 */
public class LogtalkSyntaxHighlighterColors {
  public static final String LOGTALK_KEYWORD = "LOGTALK_KEYWORD";
  public static final String LOGTALK_CLASS = "LOGTALK_CLASS";
  public static final String LOGTALK_INTERFACE = "LOGTALK_INTERFACE";
  public static final String LOGTALK_STATIC_MEMBER_FUNCTION = "LOGTALK_STATIC_MEMBER_FUNCTION";
  public static final String LOGTALK_INSTANCE_MEMBER_FUNCTION = "LOGTALK_INSTANCE_MEMBER_FUNCTION";
  public static final String LOGTALK_INSTANCE_MEMBER_VARIABLE = "LOGTALK_INSTANCE_MEMBER_VARIABLE";
  public static final String LOGTALK_STATIC_MEMBER_VARIABLE = "LOGTALK_STATIC_MEMBER_VARIABLE";
  public static final String LOGTALK_LOCAL_VARIABLE = "LOGTALK_LOCAL_VARIABLE";
  public static final String LOGTALK_PARAMETER = "LOGTALK_PARAMETER";
  public static final String LOGTALK_DEFINED_VAR = "LOGTALK_DEFINED_VAR";
  public static final String LOGTALK_UNDEFINED_VAR = "LOGTALK_UNDEFINED_VAR";

  public static final TextAttributesKey LINE_COMMENT =
    createTextAttributesKey("LOGTALK_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey BLOCK_COMMENT =
    createTextAttributesKey("LOGTALK_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
  public static final TextAttributesKey DOC_COMMENT =
    createTextAttributesKey("LOGTALK_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT);

  public static final TextAttributesKey DEFINED_VAR = createTextAttributesKey("LOGTALK_DEFINED_VAR");
  public static final TextAttributesKey UNDEFINED_VAR = createTextAttributesKey("LOGTALK_UNDEFINED_VAR");
  public static final TextAttributesKey CONDITIONALLY_NOT_COMPILED = createTextAttributesKey("LOGTALK_CONDITIONALLY_NOT_COMPILED");

  public static final TextAttributesKey KEYWORD =
    createTextAttributesKey("LOGTALK_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey NUMBER =
    createTextAttributesKey("LOGTALK_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
  public static final TextAttributesKey STRING =
    createTextAttributesKey("LOGTALK_STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey OPERATION_SIGN =
    createTextAttributesKey("LOGTALK_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
  public static final TextAttributesKey PARENTHS =
    createTextAttributesKey("LOGTALK_PARENTH", DefaultLanguageHighlighterColors.PARENTHESES);
  public static final TextAttributesKey BRACKETS =
    createTextAttributesKey("LOGTALK_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
  public static final TextAttributesKey BRACES =
    createTextAttributesKey("LOGTALK_BRACES", DefaultLanguageHighlighterColors.BRACES);
  public static final TextAttributesKey COMMA = createTextAttributesKey("LOGTALK_COMMA", DefaultLanguageHighlighterColors.COMMA);
  public static final TextAttributesKey DOT = createTextAttributesKey("LOGTALK_DOT", DefaultLanguageHighlighterColors.DOT);
  public static final TextAttributesKey SEMICOLON =
    createTextAttributesKey("LOGTALK_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
  public static final TextAttributesKey BAD_CHARACTER =
    createTextAttributesKey("LOGTALK_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
  public static final TextAttributesKey CLASS =
    createTextAttributesKey(LOGTALK_CLASS, DefaultLanguageHighlighterColors.CLASS_NAME);
  public static final TextAttributesKey INTERFACE =
    createTextAttributesKey(LOGTALK_INTERFACE, DefaultLanguageHighlighterColors.INTERFACE_NAME);
  public static final TextAttributesKey STATIC_MEMBER_FUNCTION =
    createTextAttributesKey(LOGTALK_STATIC_MEMBER_FUNCTION, DefaultLanguageHighlighterColors.STATIC_METHOD);
  public static final TextAttributesKey INSTANCE_MEMBER_FUNCTION =
    createTextAttributesKey(LOGTALK_INSTANCE_MEMBER_FUNCTION, DefaultLanguageHighlighterColors.INSTANCE_METHOD);
  public static final TextAttributesKey INSTANCE_MEMBER_VARIABLE =
    createTextAttributesKey(LOGTALK_INSTANCE_MEMBER_VARIABLE, DefaultLanguageHighlighterColors.INSTANCE_FIELD);
  public static final TextAttributesKey STATIC_MEMBER_VARIABLE =
    createTextAttributesKey(LOGTALK_STATIC_MEMBER_VARIABLE, DefaultLanguageHighlighterColors.STATIC_FIELD);
  public static final TextAttributesKey LOCAL_VARIABLE =
    createTextAttributesKey(LOGTALK_LOCAL_VARIABLE, DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
  public static final TextAttributesKey PARAMETER =
    createTextAttributesKey(LOGTALK_PARAMETER, DefaultLanguageHighlighterColors.PARAMETER);
}
