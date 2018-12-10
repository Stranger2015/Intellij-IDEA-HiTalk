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
package com.intellij.plugins.logtalk.ide;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.plugins.logtalk.logtalklib.LogtalklibCache;
import com.intellij.plugins.haxe.hxml.HXMLLanguage;
import com.intellij.plugins.haxe.hxml.psi.HXMLLib;
import com.intellij.plugins.haxe.hxml.psi.HXMLTypes;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by as3boyan on 15.11.14.
 */
public class HXMLLogtalklibCompletionContributor extends CompletionContributor {

  protected static List<String> availableLogtalklibs = null;
  protected static List<String> localLogtalklibs = null;

  public static final Logger LOGGER = Logger.getInstance("com.intellij.plugins.logtalk.ide.HXMLLogtalklibCompletionProvider");

  public HXMLLogtalklibCompletionContributor() {
    LogtalklibCache haxelibCache = LogtalklibCache.getInstance();
    availableLogtalklibs = haxelibCache.getAvailableLogtalklibs();
    localLogtalklibs = haxelibCache.getLocalLogtalklibs();

    extend(CompletionType.BASIC, psiElement(HXMLTypes.VALUE).withParent(HXMLLib.class).withLanguage(HXMLLanguage.INSTANCE),
           new CompletionProvider<CompletionParameters>() {
             @Override
             protected void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet result) {
               for (int i = 0; i < availableLogtalklibs.size(); i++) {
                 result.addElement(LookupElementBuilder.create(availableLogtalklibs.get(i))
                                     .withTailText(" available at haxelib", true));
               }

               for (int i = 0; i < localLogtalklibs.size(); i++) {
                 result.addElement(LookupElementBuilder.create(localLogtalklibs.get(i))
                                     .withTailText(" installed", true));
               }
             }
           });
  }
}
