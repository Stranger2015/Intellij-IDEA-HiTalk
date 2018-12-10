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
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.plugins.haxe.LogtalkLanguage;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypeSets;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.LogtalkCodeGenerateUtil;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.LogtalkLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkKeywordCompletionContributor extends CompletionContributor {
  private static final Set<String> allowedKeywords = new THashSet<String>() {
    {
      for (IElementType elementType : LogtalkTokenTypeSets.KEYWORDS.getTypes()) {
        add(elementType.toString());
      }
    }
  };

  public LogtalkKeywordCompletionContributor() {
    final PsiElementPattern.Capture<PsiElement> idInExpression =
      psiElement().withSuperParent(1, LogtalkIdentifier.class).withSuperParent(2, LogtalkReference.class);
    final PsiElementPattern.Capture<PsiElement> inComplexExpression =
      psiElement().withSuperParent(2, psiElement().withFirstChild(StandardPatterns.instanceOf(LogtalkReference.class)));

    final PsiElementPattern.Capture<PsiElement> inheritPattern =
      psiElement().inFile(StandardPatterns.instanceOf(LogtalkFile.class)).withSuperParent(1, PsiErrorElement.class).
        and(psiElement().withSuperParent(2, LogtalkInheritList.class));
    extend(CompletionType.BASIC,
           psiElement().andOr(psiElement().withSuperParent(1, PsiErrorElement.class),
                              psiElement().withSuperParent(1, GeneratedParserUtilBase.DummyBlock.class)).
             andOr(psiElement().withSuperParent(2, LogtalkClassBody.class), psiElement().withSuperParent(2, LogtalkInheritList.class)),
           new CompletionProvider<CompletionParameters>() {
             @Override
             protected void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet result) {
               result.addElement(LookupElementBuilder.create("extends"));
               result.addElement(LookupElementBuilder.create("implements"));
             }
           });
    // foo.b<caret> - bad
    // i<caret> - good
    extend(CompletionType.BASIC,
           psiElement().inFile(StandardPatterns.instanceOf(LogtalkFile.class)).andNot(idInExpression.and(inComplexExpression))
             .andNot(inheritPattern),
           new CompletionProvider<CompletionParameters>() {
             @Override
             protected void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet result) {
               final Collection<String> suggestedKeywords = suggestKeywords(parameters.getPosition());
               suggestedKeywords.retainAll(allowedKeywords);
               for (String keyword : suggestedKeywords) {
                 result.addElement(LookupElementBuilder.create(keyword));
               }
             }
           });
  }

  private static Collection<String> suggestKeywords(PsiElement position) {
    final TextRange posRange = position.getTextRange();
    final LogtalkFile posFile = (LogtalkFile)position.getContainingFile();

    final List<PsiElement> pathToBlockStatement = UsefulPsiTreeUtil.getPathToParentOfType(position, LogtalkBlockStatement.class);

    final LogtalkPsiCompositeElement classInterfaceEnum =
      PsiTreeUtil.getParentOfType(position, LogtalkClassBody.class, LogtalkInterfaceBody.class, LogtalkEnumBody.class);

    final String text;
    final int offset;
    if (pathToBlockStatement != null) {
      final Pair<String, Integer> pair = LogtalkCodeGenerateUtil.wrapStatement(posRange.substring(posFile.getText()));
      text = pair.getFirst();
      offset = pair.getSecond();
    }
    else if (classInterfaceEnum != null) {
      final Pair<String, Integer> pair = LogtalkCodeGenerateUtil.wrapFunction(posRange.substring(posFile.getText()));
      text = pair.getFirst();
      offset = pair.getSecond();
    }
    else {
      text = posFile.getText().substring(0, posRange.getStartOffset());
      offset = 0;
    }

    final List<String> result = new ArrayList<String>();
    if (pathToBlockStatement != null && pathToBlockStatement.size() > 1) {
      final PsiElement blockChild = pathToBlockStatement.get(pathToBlockStatement.size() - 2);
      result.addAll(suggestBySibling(UsefulPsiTreeUtil.getPrevSiblingSkipWhiteSpacesAndComments(blockChild, true)));
    }

    PsiFile file = PsiFileFactory.getInstance(posFile.getProject()).createFileFromText("a.hx", LogtalkLanguage.INSTANCE, text, true, false);
    GeneratedParserUtilBase.CompletionState state = new GeneratedParserUtilBase.CompletionState(text.length() - offset);
    file.putUserData(GeneratedParserUtilBase.COMPLETION_STATE_KEY, state);
    TreeUtil.ensureParsed(file.getNode());
    result.addAll(state.items);

    // always
    result.add(LogtalkTokenTypes.PPIF.toString());
    result.add(LogtalkTokenTypes.PPELSE.toString());
    result.add(LogtalkTokenTypes.PPELSEIF.toString());
    result.add(LogtalkTokenTypes.PPERROR.toString());
    return result;
  }

  @NotNull
  private static Collection<? extends String> suggestBySibling(@Nullable PsiElement sibling) {
    if (LogtalkIfStatement.class.isInstance(sibling)) {
      return Collections.singletonList(LogtalkTokenTypes.KELSE.toString());
    }
    else if (LogtalkTryStatement.class.isInstance(sibling) || LogtalkCatchStatement.class.isInstance(sibling)) {
      return Collections.singletonList(LogtalkTokenTypes.KCATCH.toString());
    }

    return Collections.emptyList();
  }
}
