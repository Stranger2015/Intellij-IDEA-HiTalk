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

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.plugins.logtalk.LogtalkBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.plugins.logtalk.ide.highlight.LogtalkSyntaxHighlighterColors.*;

/**
 * @author fedor.korotkov
 */
public class LogtalkColorSettingsPage implements ColorSettingsPage {
  private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.line.comment"), LINE_COMMENT),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.block.comment"), BLOCK_COMMENT),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.doc.comment"), DOC_COMMENT),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.conditional.compilation"), CONDITIONALLY_NOT_COMPILED),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.conditional.compilation.defined.flag"), DEFINED_VAR),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.conditional.compilation.undefined.flag"), UNDEFINED_VAR),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.keyword"), KEYWORD),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.number"), NUMBER),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.string"), STRING),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.operator"), OPERATION_SIGN),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.parenths"), PARENTHS),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.brackets"), BRACKETS),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.braces"), BRACES),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.comma"), COMMA),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.dot"), DOT),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.semicolon"), SEMICOLON),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.bad.character"), BAD_CHARACTER),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.parameter"), PARAMETER),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.local.variable"), LOCAL_VARIABLE),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.class"), CLASS),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.interface"), INTERFACE),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.instance.member.function"), INSTANCE_MEMBER_FUNCTION),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.static.member.function"), STATIC_MEMBER_FUNCTION),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.instance.member.variable"), INSTANCE_MEMBER_VARIABLE),
    new AttributesDescriptor(LogtalkBundle.message("logtalk.color.settings.description.static.member.variable"), STATIC_MEMBER_VARIABLE)
  };

  @NonNls private static final Map<String, TextAttributesKey> ourTags = new HashMap<String, TextAttributesKey>();

  static {
    ourTags.put("parameter", PARAMETER);
    ourTags.put("local.variable", LOCAL_VARIABLE);
    ourTags.put("class", CLASS);
    ourTags.put("compilation", CONDITIONALLY_NOT_COMPILED);
    ourTags.put("defined.flag", DEFINED_VAR);
    ourTags.put("undefined.flag", UNDEFINED_VAR);
    ourTags.put("interface", INTERFACE);
    ourTags.put("instance.member.function", INSTANCE_MEMBER_FUNCTION);
    ourTags.put("static.member.function", STATIC_MEMBER_FUNCTION);
    ourTags.put("instance.member.variable", INSTANCE_MEMBER_VARIABLE);
    ourTags.put("static.member.variable", STATIC_MEMBER_VARIABLE);
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return LogtalkBundle.message("logtalk.title");
  }

  @Override
  public Icon getIcon() {
    return icons.LogtalkIcons.Logtalk_16;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return new LogtalkSyntaxHighlighter(null);
  }

  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return ourTags;
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return ATTRS;
  }

  @NotNull
  @Override
  public String getDemoText() {
    return "<compilation>#if <defined.flag>definedFlag</defined.flag> && <undefined.flag>undefinedFlag</undefined.flag>\n" +
           "#error \"Error!!\"\n" +
           "#else</compilation>\n" +
           "import <class>util.Date</class>;\n" +
           "<compilation>#end</compilation>\n" +
           "\n" +
           "/* Block comment */\n" +
           "/**\n" +
           " Document comment\n" +
           "**/\n" +
           "class <class>SomeClass</class> implements <interface>IOther</interface> { // some comment\n" +
           "  private var <instance.member.variable>field</instance.member.variable> = null;\n" +
           "  private var <instance.member.variable>unusedField</instance.member.variable>:<class>Number</class> = 12345.67890;\n" +
           "  private var <instance.member.variable>anotherString</instance.member.variable>:<class>String</class> = \"Another\\nStrin\\g\";\n" +
           "  public static var <static.member.variable>staticField</static.member.variable>:<class>Int</class> = 0;\n" +
           "\n" +
           "  public static function <static.member.function>inc</static.member.function>() {\n" +
           "    <static.member.variable>staticField</static.member.variable>++;\n" +
           "  }\n" +
           "  public function <instance.member.function>foo</instance.member.function>(<parameter>param</parameter>:<interface>AnInterface</interface>) {\n" +
           "    trace(<instance.member.variable>anotherString</instance.member.variable> + <parameter>param</parameter>);\n" +
           "    var <local.variable>reassignedValue</local.variable>:<class>Int</class> = <class>SomeClass</class>.<static.member.variable>staticField</static.member.variable>; \n" +
           "    <local.variable>reassignedValue</local.variable> ++; \n" +
           "    function localFunction() {\n" +
           "      var <local.variable>a</local.variable>:<class>Int</class> = $$$;// bad character\n" +
           "    };\n" +
           "  }\n" +
           "}";
  }
}
