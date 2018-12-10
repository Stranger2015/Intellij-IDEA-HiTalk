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
package com.intellij.plugins.logtalk.util;

import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.LogtalkImportModel;
import com.intellij.plugins.logtalk.lang.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.util.SmartList;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class LogtalkImportUtil {
  public static List<LogtalkImportStatement> findUnusedImports(PsiFile file) {
    final Collection<PsiElement> externalReferences = getExternalReferences(file);

    List<LogtalkImportStatement> allImportStatements = ((LogtalkFile)file).getImportStatements();

    final boolean hasWildcards = allImportStatements.stream().anyMatch(statement -> statement.getModel().hasWildcard());

    List<LogtalkImportStatement> usefulStatements = allImportStatements
      .stream()
      .filter(statement -> externalReferences
        .stream()
        .anyMatch(referencedElement -> isStatementExposesReference(statement, referencedElement)))
      .distinct()
      .collect(Collectors.toList());

    if (hasWildcards) {
      removeUnusedWildcards(externalReferences, usefulStatements);
    }

    usefulStatements.forEach(allImportStatements::remove);

    return allImportStatements;
  }

  public static boolean isStatementExposesReference(LogtalkImportStatement statement, PsiElement referencedElement) {
    return exposeReference(statement, referencedElement) != null;
  }

  public static PsiElement exposeReference(LogtalkImportStatement statement, PsiElement referencedElement) {
    PsiElement result = null;
    if (referencedElement instanceof LogtalkNamedComponent) {
      result = statement.getModel().exposeByName(((LogtalkNamedComponent)referencedElement).getName());
    }
    if (result == null && referencedElement instanceof LogtalkReference) {
      result = statement.getModel().exposeByName(referencedElement.getText());
    }
    return result;
  }

  private static void removeUnusedWildcards(Collection<PsiElement> classesInFile, List<LogtalkImportStatement> usefulStatements) {
    final MultiMap<PsiElement, LogtalkImportModel> referenceMap = new MultiMap<>();

    usefulStatements.forEach(statement -> classesInFile.forEach(referencedElement -> {
      if (referencedElement instanceof LogtalkClass ||
          referencedElement instanceof LogtalkVarDeclaration ||
          referencedElement instanceof LogtalkMethod) {
        LogtalkNamedComponent component = (LogtalkNamedComponent)referencedElement;
        if (statement.getModel().exposeByName(component.getName()) != null) {
          referenceMap.putValue(referencedElement, statement.getModel());
        }
      }
      else if (referencedElement instanceof LogtalkReference) {
        if (statement.getModel().exposeByName(referencedElement.getText()) != null) {
          referenceMap.putValue(referencedElement, statement.getModel());
        }
      }
    }));

    List<LogtalkImportModel> uniqueWildcards = new SmartList<>();
    List<LogtalkImportModel> notUniqueWildcards = new SmartList<>();
    for (PsiElement key : referenceMap.keySet()) {
      Collection<LogtalkImportModel> imports = referenceMap.get(key);
      for (LogtalkImportModel statement : imports) {
        if (statement.hasWildcard()) {
          if (imports.size() > 1 && !uniqueWildcards.contains(statement) && !notUniqueWildcards.contains(statement)) {
            notUniqueWildcards.add(statement);
          }
          if (imports.size() == 1 && !uniqueWildcards.contains(statement)) {
            uniqueWildcards.add(statement);
          }
        }
      }
    }

    notUniqueWildcards.forEach(model -> usefulStatements.remove(model.getBasePsi()));
  }


  public static Collection<PsiElement> getExternalReferences(@NotNull PsiFile file) {
    final Map<PsiElement, PsiElement> result = new HashMap<>();

    file.acceptChildren(new LogtalkRecursiveVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof LogtalkPackageStatement || element instanceof LogtalkImportStatement || element instanceof LogtalkUsingStatement) return;
        if (element instanceof LogtalkReference) {
          LogtalkReference reference = (LogtalkReference)element;
          PsiElement referencedElement = reference.resolve();
          if ((!(reference.isQualified() || referencedElement instanceof PsiPackage) || (reference.isQualified() && referencedElement instanceof LogtalkClass)) &&
              isApplicableExternalReference(referencedElement)) {
            result.put(referencedElement, element);
          }
        }

        super.visitElement(element);
      }

      private boolean isApplicableExternalReference(PsiElement reference) {
        return reference != null && !result.containsKey(reference) && reference.getContainingFile() != file;
      }

      @Override
      public void visitImportStatement(@NotNull LogtalkImportStatement o) {
      }
    });

    result.values().forEach(element -> System.out.println(((LogtalkReference)element).getReferenceNameElement().getText()));
    return result.values();
  }
}
