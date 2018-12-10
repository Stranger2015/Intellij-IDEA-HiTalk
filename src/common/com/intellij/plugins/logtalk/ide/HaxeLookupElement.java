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

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.JavaCompletionUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugins.haxe.build.FieldWrapper;
import com.intellij.plugins.haxe.build.IdeaTarget;
import com.intellij.plugins.haxe.lang.psi.LogtalkClassResolveResult;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponentName;
import com.intellij.plugins.haxe.lang.psi.LogtalkReference;
import com.intellij.plugins.haxe.model.LogtalkMemberModel;
import com.intellij.plugins.haxe.model.LogtalkMethodContext;
import com.intellij.plugins.haxe.model.LogtalkMethodModel;
import com.intellij.plugins.haxe.model.LogtalkModifierType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkLookupElement extends LookupElement {
  private final LogtalkComponentName myComponentName;
  private final LogtalkClassResolveResult leftReference;
  private final LogtalkMethodContext context;

  public static Collection<LogtalkLookupElement> convert(LogtalkClassResolveResult leftReferenceResolveResult,
                                                      @NotNull Collection<LogtalkComponentName> componentNames,
                                                      @NotNull Collection<LogtalkComponentName> componentNamesExtension) {
    final List<LogtalkLookupElement> result = new ArrayList<>(componentNames.size());
    for (LogtalkComponentName componentName : componentNames) {
      LogtalkMethodContext context = null;
      if (componentNamesExtension.contains(componentName)) {
        context = LogtalkMethodContext.EXTENSION;
      } else {
        context = LogtalkMethodContext.NO_EXTENSION;
      }
      result.add(new LogtalkLookupElement(leftReferenceResolveResult, componentName, context));
    }
    return result;
  }

  public LogtalkLookupElement(LogtalkClassResolveResult leftReference, LogtalkComponentName name, LogtalkMethodContext context) {
    this.leftReference = leftReference;
    this.myComponentName = name;
    this.context = context;
  }

  @NotNull
  @Override
  public String getLookupString() {
    return myComponentName.getIdentifier().getText();
  }

  @Override
  public void renderElement(LookupElementPresentation presentation) {
    final ItemPresentation myComponentNamePresentation = myComponentName.getPresentation();
    if (myComponentNamePresentation == null) {
      presentation.setItemText(getLookupString());
      return;
    }

    String presentableText = myComponentNamePresentation.getPresentableText();

    // Check for members: methods and fields
    LogtalkMemberModel member = LogtalkMemberModel.fromPsi(myComponentName);

    if (member != null) {
      presentableText = member.getPresentableText(context);

      // Check deprecated modifiers
      if (member.getModifiers().hasModifier(LogtalkModifierType.DEPRECATED)) {
        presentation.setStrikeout(true);
      }

      // Check for non-inherited members to highlight them as intellij-java does
      // @TODO: Self members should be displayed first!
      if (leftReference != null) {
        if (member.getDeclaringClass().getPsi() == leftReference.getLogtalkClass()) {
          presentation.setItemTextBold(true);
        }
      }
    }

    presentation.setItemText(presentableText);
    presentation.setIcon(myComponentNamePresentation.getIcon(true));
    final String pkg = myComponentNamePresentation.getLocationString();
    if (StringUtil.isNotEmpty(pkg)) {
      presentation.setTailText(" " + pkg, true);
    }
  }

  @Override
  public void handleInsert(InsertionContext context) {
    LogtalkMemberModel memberModel = LogtalkMemberModel.fromPsi(myComponentName);
    boolean hasParams = false;
    boolean isMethod = false;
    if (memberModel != null) {
      if (memberModel instanceof LogtalkMethodModel) {
        isMethod = true;
        LogtalkMethodModel methodModel = (LogtalkMethodModel)memberModel;
        hasParams = !methodModel.getParametersWithContext(this.context).isEmpty();
      }
    }

    if (isMethod) {
      final LookupElement[] allItems = context.getElements();
      final boolean overloadsMatter = allItems.length == 1 && getUserData(FORCE_SHOW_SIGNATURE_ATTR) == null;
      JavaCompletionUtil.insertParentheses(context, this, overloadsMatter, hasParams);
    }
  }

  private static final Key<Boolean> FORCE_SHOW_SIGNATURE_ATTR =
    IdeaTarget.IS_VERSION_15_COMPATIBLE ? new FieldWrapper<Key<Boolean>>(JavaCompletionUtil.class,
                                                                         "FORCE_SHOW_SIGNATURE_ATTR").get(null)
                                        : Key.<Boolean>create("forceShowSignature");

  @NotNull
  @Override
  public Object getObject() {
    return myComponentName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LogtalkLookupElement)) return false;

    return myComponentName.equals(((LogtalkLookupElement)o).myComponentName);
  }

  @Override
  public int hashCode() {
    return myComponentName.hashCode();
  }
}
