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

import com.intellij.codeInspection.*;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.ide.LogtalkImportOptimizer;
import com.intellij.plugins.haxe.lang.psi.LogtalkImportStatement;
import com.intellij.plugins.haxe.util.LogtalkImportUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.ide.LogtalkImportOptimizer;
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
public class LogtalkUnusedImportInspection extends LocalInspectionTool {
  @NotNull
  public String getGroupDisplayName() {
    return LogtalkBundle.message("inspections.group.name");
  }

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return LogtalkBundle.message("logtalk.inspection.unused.import.name");
  }

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }

  @NotNull
  @Override
  public String getShortName() {
    return "LogtalkUnusedImport";
  }

  @Nullable
  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    List<LogtalkImportStatement> unusedImports = LogtalkImportUtil.findUnusedImports(file);

    if (unusedImports.isEmpty()) {
      return ProblemDescriptor.EMPTY_ARRAY;
    }
    final List<ProblemDescriptor> result = new ArrayList<>();
    for (LogtalkImportStatement haxeImportStatement : unusedImports) {
      result.add(manager.createProblemDescriptor(
        haxeImportStatement,
        TextRange.from(0, haxeImportStatement.getTextLength()),
        getDisplayName(),
        ProblemHighlightType.LIKE_UNUSED_SYMBOL,
        isOnTheFly,
        OPTIMIZE_IMPORTS_FIX
      ));
    }

    return ArrayUtil.toObjectArray(result, ProblemDescriptor.class);
  }

  private static final LocalQuickFix OPTIMIZE_IMPORTS_FIX = new LocalQuickFix() {
    @NotNull
    @Override
    public String getName() {
      return LogtalkBundle.message("logtalk.fix.optimize.imports");
    }

    @NotNull
    public String getFamilyName() {
      return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement psiElement = descriptor.getPsiElement();
      invoke(project, psiElement.getContainingFile());
    }

    public void invoke(@NotNull final Project project, PsiFile file) {
      ImportOptimizer optimizer = new LogtalkImportOptimizer();
      final Runnable runnable = optimizer.processFile(file);
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        public void run() {
          CommandProcessor.getInstance().executeCommand(project, runnable, getFamilyName(), this);
        }
      });
    }
  };
}
