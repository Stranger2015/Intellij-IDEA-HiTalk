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
package com.intellij.plugins.logtalk.ide.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.ide.annotator.LogtalkAnnotatingVisitor;
import com.intellij.plugins.haxe.lang.psi.LogtalkReferenceExpression;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.ide.annotator.LogtalkAnnotatingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fedorkorotkov.
 */
public class LogtalkUnresolvedSymbolInspection extends LocalInspectionTool {
  @NotNull
  public String getGroupDisplayName() {
    return LogtalkBundle.message("inspections.group.name");
  }

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return LogtalkBundle.message("logtalk.inspection.unresolved.symbol");
  }

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }

  @NotNull
  @Override
  public String getShortName() {
    return "LogtalkUnresolvedSymbol";
  }

  @Nullable
  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
    final List<ProblemDescriptor> result = new ArrayList<ProblemDescriptor>();
    new LogtalkAnnotatingVisitor() {
      @Override
      protected void handleUnresolvedReference(LogtalkReferenceExpression reference) {
        PsiElement nameIdentifier = reference.getReferenceNameElement();
        if (nameIdentifier == null) return;
        result.add(manager.createProblemDescriptor(
          nameIdentifier,
          TextRange.from(0, nameIdentifier.getTextLength()),
          getDisplayName(),
          ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
          isOnTheFly
        ));
      }
    }.visitFile(file);
    return ArrayUtil.toObjectArray(result, ProblemDescriptor.class);
  }
}
