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

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponentName;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.haxe.util.LogtalkPresentableUtil;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkDocumentationProvider implements DocumentationProvider {

  /*
    provides ctrl+hover info
   */
  @Override
  public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
    return null;
  }

  @Override
  public String generateDoc(PsiElement element, PsiElement originalElement) {
    if (!(element instanceof LogtalkComponentName) && !(element instanceof LogtalkNamedComponent)) {
      return null;
    }
    LogtalkNamedComponent namedComponent = (LogtalkNamedComponent)(element instanceof LogtalkNamedComponent ? element : element.getParent());
    final StringBuilder builder = new StringBuilder();
    final LogtalkComponentType type = LogtalkComponentType.typeOf(namedComponent);
    if (namedComponent instanceof LogtalkClass) {
      builder.append(((LogtalkClass)namedComponent).getQualifiedName());
    }
    else if (type == LogtalkComponentType.FIELD || type == LogtalkComponentType.METHOD) {
      final LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(namedComponent, LogtalkClass.class);
      assert haxeClass != null;
      builder.append(haxeClass.getQualifiedName());
      builder.append(" ");
      builder.append(type.toString().toLowerCase());
      builder.append(" ");
      builder.append(namedComponent.getName());
    }
    final PsiComment comment = LogtalkResolveUtil.findDocumentation(namedComponent);
    if (comment != null) {
      builder.append("<br/>");
      builder.append(LogtalkPresentableUtil.unwrapCommentDelimiters(comment.getText()));
    }
    return builder.toString();
  }

  @Override
  public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
    return null;
  }

  @Override
  public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
    return null;
  }

  @Override
  public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
    return null;
  }
}
