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
package com.intellij.plugins.logtalk.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.plugins.logtalk.ide.index.LogtalkClassInfo;
import com.intellij.plugins.logtalk.ide.index.LogtalkComponentIndex;
import com.intellij.plugins.logtalk.lang.psi.LogtalkRecursiveVisitor;
import com.intellij.plugins.logtalk.lang.psi.LogtalkReference;
import com.intellij.plugins.logtalk.model.LogtalkModel;
import com.intellij.plugins.logtalk.util.LogtalkAddImportHelper;
import com.intellij.plugins.logtalk.util.LogtalkElementGenerator;
import com.intellij.plugins.logtalk.util.LogtalkResolveUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.plugins.logtalk.ide.completion.LogtalkCommonCompletionPattern.*;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkClassNameCompletionContributor extends CompletionContributor {
  public LogtalkClassNameCompletionContributor() {

    extend(CompletionType.BASIC,
           psiElement().and(inImportOrUsing),
           new CompletionProvider<CompletionParameters>() {
             @Override
             protected void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet result) {
               LogtalkReference reference = PsiTreeUtil.getParentOfType(parameters.getPosition(), LogtalkReference.class);
               String packagePrefix = reference != null && reference.isQualified() ? reference.getQualifier().getText() : null;
               addVariantsFromIndex(result, parameters.getOriginalFile(), packagePrefix, FULL_PATH_INSERT_HANDLER);
             }
           });

    extend(CompletionType.BASIC,
           isSimpleIdentifier.andNot(inImportOrUsing),
           new CompletionProvider<CompletionParameters>() {
             @Override
             protected void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet result) {
               final PsiFile file = parameters.getOriginalFile();

               addVariantsFromIndex(result, file, null, CLASS_INSERT_HANDLER);
               addVariantsFromImports(result, file);
             }
           });

    extend(CompletionType.BASIC,
           inComplexExpression.andNot(inImportOrUsing),
           new CompletionProvider<CompletionParameters>() {
             @Override
             protected void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet result) {
               LogtalkReference leftReference =
                 LogtalkResolveUtil.getLeftReference(PsiTreeUtil.getParentOfType(parameters.getPosition(), LogtalkReference.class));
               PsiElement leftTarget = leftReference != null ? leftReference.resolve() : null;
               if (leftTarget instanceof PsiPackage) {
                 addVariantsFromIndex(result, parameters.getOriginalFile(), ((PsiPackage)leftTarget).getQualifiedName(), null);
               }
             }
           });
  }

  private static void addVariantsFromIndex(final CompletionResultSet resultSet,
                                           final PsiFile targetFile,
                                           @Nullable String prefixPackage,
                                           @Nullable final InsertHandler<LookupElement> insertHandler) {
    final Project project = targetFile.getProject();
    final GlobalSearchScope scope = LogtalkResolveUtil.getScopeForElement(targetFile);
    final MyProcessor processor = new MyProcessor(resultSet, prefixPackage, insertHandler);
    LogtalkComponentIndex.processAll(project, processor, scope);
  }

  private static void addVariantsFromImports(final CompletionResultSet resultSet,
                                             final PsiFile targetFile) {
    targetFile.acceptChildren(new LogtalkRecursiveVisitor() {
      @Override
      public void visitImportStatement(@NotNull LogtalkImportStatement importStatement) {
        final List<LogtalkModel> exposedMembers = importStatement.getModel().getExposedMembers();
        final String alias = importStatement.getAlias() != null ? importStatement.getAlias().getText() : null;

        for (int i = 0, size = exposedMembers.size(); i < size; i++) {
          LogtalkModel member = exposedMembers.get(i);
          LookupElementBuilder lookupElement = LogtalkLookupElementFactory.create(member, alias);
          if (lookupElement != null) {
            resultSet.addElement(lookupElement);
          }
          if (alias != null) {
            return;
          }
        }
      }
    });
  }


  private static final InsertHandler<LookupElement> CLASS_INSERT_HANDLER =
    (context, item) -> addImportForLookupElement(context, item, context.getTailOffset() - 1);

  private static void addImportForLookupElement(final InsertionContext context, final LookupElement item, final int tailOffset) {
    final PsiReference ref = context.getFile().findReferenceAt(tailOffset);
    if (ref == null || ref.resolve() != null) {
      // no import statement needed
      return;
    }
    new WriteCommandAction(context.getProject(), context.getFile()) {
      @Override
      protected void run(@NotNull Result result) throws Throwable {
        final String importPath = (String)item.getObject();
        LogtalkAddImportHelper.addImport(importPath, context.getFile());
      }
    }.execute();
  }

  /**
   * Full path insert handler
   **/
  private static final InsertHandler<LookupElement> FULL_PATH_INSERT_HANDLER =
    (context, item) -> replaceElementToFullPath(context, item, context.getTailOffset() - 1);

  private static void replaceElementToFullPath(final InsertionContext context, final LookupElement item, final int tailOffset) {
    new WriteCommandAction(context.getProject(), context.getFile()) {
      @Override
      protected void run(@NotNull Result result) throws Throwable {
        final String importPath = (String)item.getObject();
        final PsiReference currentReference = context.getFile().findReferenceAt(context.getTailOffset() - 1);
        if (currentReference != null && currentReference.getElement() != null) {
          final PsiElement currentElement = currentReference.getElement();
          final LogtalkReference fullPathReference = LogtalkElementGenerator.createReferenceFromText(context.getProject(), importPath);
          if (fullPathReference != null) {
            currentElement.replace(fullPathReference);
          }
        }
      }
    }.execute();
  }

  private static class MyProcessor implements Processor<Pair<String, LogtalkClassInfo>> {
    private final CompletionResultSet myResultSet;
    @Nullable private final InsertHandler<LookupElement> myInsertHandler;
    @Nullable private final String myPrefixPackage;

    private MyProcessor(CompletionResultSet resultSet,
                        @Nullable String prefixPackage,
                        @Nullable InsertHandler<LookupElement> insertHandler) {
      myResultSet = resultSet;
      myPrefixPackage = prefixPackage;
      myInsertHandler = insertHandler;
    }

    @Override
    public boolean process(Pair<String, LogtalkClassInfo> pair) {
      LogtalkClassInfo info = pair.getSecond();
      if (myPrefixPackage == null || myPrefixPackage.equalsIgnoreCase(info.getValue())) {
        String name = pair.getFirst();
        final String qName = LogtalkResolveUtil.joinQName(info.getValue(), name);
        myResultSet.addElement(LookupElementBuilder.create(qName, name)
                                 .withIcon(info.getCompletionIcon())
                                 .withTailText(" " + info.getValue(), true)
                                 .withInsertHandler(myInsertHandler));
      }
      return true;
    }
  }
}
