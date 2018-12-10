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
package com.intellij.plugins.logtalk.ide.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.ide.refactoring.LogtalkRefactoringUtil;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.LogtalkElementGenerator;
import com.intellij.plugins.haxe.util.LogtalkNameSuggesterUtil;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.plugins.logtalk.ide.refactoring.LogtalkRefactoringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.refactoring.introduce.inplace.OccurrencesChooser;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author: Fedor.Korotkov
 */
@SuppressWarnings("MethodMayBeStatic")
public abstract class LogtalkIntroduceHandler implements RefactoringActionHandler {
  @Nullable
  protected static PsiElement findAnchor(PsiElement occurrence) {
    return findAnchor(Arrays.asList(occurrence));
  }

  @Nullable
  protected static PsiElement findAnchor(List<PsiElement> occurrences) {
    PsiElement anchor = occurrences.get(0);
    next:
    do {
      final LogtalkBlockStatement block = PsiTreeUtil.getParentOfType(anchor, LogtalkBlockStatement.class);

      int minOffset = Integer.MAX_VALUE;
      for (PsiElement element : occurrences) {
        minOffset = Math.min(minOffset, element.getTextOffset());
        if (!PsiTreeUtil.isAncestor(block, element, true)) {
          anchor = block;
          continue next;
        }
      }

      if (block == null) {
        return null;
      }

      PsiElement child = null;
      PsiElement[] children = block.getChildren();
      for (PsiElement aChildren : children) {
        child = aChildren;
        if (child.getTextRange().contains(minOffset)) {
          break;
        }
      }

      return child;
    }
    while (true);
  }

  protected final String myDialogTitle;

  public LogtalkIntroduceHandler(@NotNull final String dialogTitle) {
    myDialogTitle = dialogTitle;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
    performAction(new LogtalkIntroduceOperation(project, editor, file, null));
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
  }

  protected void performAction(LogtalkIntroduceOperation operation) {
    final PsiFile file = operation.getFile();
    if (!CommonRefactoringUtil.checkReadOnlyStatus(file)) {
      return;
    }
    final Editor editor = operation.getEditor();
    if (editor.getSettings().isVariableInplaceRenameEnabled()) {
      final TemplateState templateState = TemplateManagerImpl.getTemplateState(operation.getEditor());
      if (templateState != null && !templateState.isFinished()) {
        return;
      }
    }

    PsiElement element1 = null;
    PsiElement element2 = null;
    final SelectionModel selectionModel = editor.getSelectionModel();
    if (selectionModel.hasSelection()) {
      element1 = file.findElementAt(selectionModel.getSelectionStart());
      element2 = file.findElementAt(selectionModel.getSelectionEnd() - 1);
      if (element1 instanceof PsiWhiteSpace) {
        int startOffset = element1.getTextRange().getEndOffset();
        element1 = file.findElementAt(startOffset);
      }
      if (element2 instanceof PsiWhiteSpace) {
        int endOffset = element2.getTextRange().getStartOffset();
        element2 = file.findElementAt(endOffset - 1);
      }
    }
    else {
      if (smartIntroduce(operation)) {
        return;
      }
      final CaretModel caretModel = editor.getCaretModel();
      final Document document = editor.getDocument();
      int lineNumber = document.getLineNumber(caretModel.getOffset());
      if ((lineNumber >= 0) && (lineNumber < document.getLineCount())) {
        element1 = file.findElementAt(document.getLineStartOffset(lineNumber));
        element2 = file.findElementAt(document.getLineEndOffset(lineNumber) - 1);
      }
    }
    final Project project = operation.getProject();
    if (element1 == null || element2 == null) {
      showCannotPerformError(project, editor);
      return;
    }

    element1 = LogtalkRefactoringUtil.getSelectedExpression(project, file, element1, element2);
    if (element1 == null) {
      showCannotPerformError(project, editor);
      return;
    }

    if (!checkIntroduceContext(file, editor, element1)) {
      return;
    }
    operation.setElement(element1);
    performActionOnElement(operation);
  }

  protected boolean checkIntroduceContext(PsiFile file, Editor editor, PsiElement element) {
    if (!isValidIntroduceContext(element)) {
      showCannotPerformError(file.getProject(), editor);
      return false;
    }
    return true;
  }

  private void showCannotPerformError(Project project, Editor editor) {
    CommonRefactoringUtil.showErrorHint(
      project,
      editor,
      LogtalkBundle.message("refactoring.introduce.selection.error"),
      myDialogTitle,
      "refactoring.extractMethod"
    );
  }

  protected boolean isValidIntroduceContext(PsiElement element) {
    return PsiTreeUtil.getParentOfType(element, LogtalkParameterList.class) == null;
  }

  private boolean smartIntroduce(final LogtalkIntroduceOperation operation) {
    final Editor editor = operation.getEditor();
    final PsiFile file = operation.getFile();
    int offset = editor.getCaretModel().getOffset();
    PsiElement elementAtCaret = file.findElementAt(offset);
    if (!checkIntroduceContext(file, editor, elementAtCaret)) return true;
    final List<LogtalkExpression> expressions = new ArrayList<LogtalkExpression>();
    while (elementAtCaret != null) {
      if (elementAtCaret instanceof LogtalkFile) {
        break;
      }
      if (elementAtCaret instanceof LogtalkExpression) {
        expressions.add((LogtalkExpression)elementAtCaret);
      }
      elementAtCaret = elementAtCaret.getParent();
    }
    if (expressions.size() == 1 || ApplicationManager.getApplication().isUnitTestMode()) {
      operation.setElement(expressions.get(0));
      performActionOnElement(operation);
      return true;
    }
    else if (expressions.size() > 1) {
      IntroduceTargetChooser.showChooser(
        editor,
        expressions,
        new Pass<LogtalkExpression>() {
          @Override
          public void pass(LogtalkExpression expression) {
            operation.setElement(expression);
            performActionOnElement(operation);
          }
        }, new Function<LogtalkExpression, String>() {
          public String fun(LogtalkExpression expression) {
            return expression.getText();
          }
        }
      );
      return true;
    }
    return false;
  }

  private void performActionOnElement(LogtalkIntroduceOperation operation) {
    if (!checkEnabled(operation)) {
      return;
    }
    final PsiElement element = operation.getElement();

    final LogtalkExpression initializer = (LogtalkExpression)element;
    operation.setInitializer(initializer);

    operation.setOccurrences(getOccurrences(element, initializer));
    operation.setSuggestedNames(getSuggestedNames(initializer));
    if (operation.getOccurrences().size() == 0) {
      operation.setReplaceAll(false);
    }

    performActionOnElementOccurrences(operation);
  }

  protected void performActionOnElementOccurrences(final LogtalkIntroduceOperation operation) {
    final Editor editor = operation.getEditor();
    if (editor.getSettings().isVariableInplaceRenameEnabled()) {
      ensureName(operation);
      if (operation.isReplaceAll() != null) {
        performInplaceIntroduce(operation);
      }
      else {
        OccurrencesChooser.simpleChooser(editor).showChooser(
          operation.getElement(),
          operation.getOccurrences(),
          new Pass<OccurrencesChooser.ReplaceChoice>() {
            @Override
            public void pass(OccurrencesChooser.ReplaceChoice replaceChoice) {
              operation.setReplaceAll(replaceChoice == OccurrencesChooser.ReplaceChoice.ALL);
              performInplaceIntroduce(operation);
            }
          });
      }
    }
    else {
      performIntroduceWithDialog(operation);
    }
  }

  protected boolean checkEnabled(LogtalkIntroduceOperation operation) {
    return true;
  }

  protected static void ensureName(LogtalkIntroduceOperation operation) {
    if (operation.getName() == null) {
      final Collection<String> suggestedNames = operation.getSuggestedNames();
      if (suggestedNames.size() > 0) {
        operation.setName(suggestedNames.iterator().next());
      }
      else {
        operation.setName("x");
      }
    }
  }


  protected List<PsiElement> getOccurrences(PsiElement element, @NotNull final LogtalkExpression expression) {
    PsiElement context = element;
    LogtalkComponentType type = null;
    do {
      context = PsiTreeUtil.getParentOfType(context, LogtalkComponent.class, true);
      type = LogtalkComponentType.typeOf(context);
    }
    while (type != null && notFunctionMethodClass(type));  // XXX-EBatTiVo: Probably should not stop if type == null.
    if (context == null) {
      context = expression.getContainingFile();
    }
    return LogtalkRefactoringUtil.getOccurrences(expression, context);
  }

  private static boolean notFunctionMethodClass(LogtalkComponentType type) {
    final boolean isFunctionMethodClass = type == LogtalkComponentType.METHOD ||
                                          type == LogtalkComponentType.FUNCTION ||
                                          type == LogtalkComponentType.INTERFACE ||
                                          type == LogtalkComponentType.CLASS;
    return !isFunctionMethodClass;
  }

  protected Collection<String> getSuggestedNames(final LogtalkExpression expression) {
    Collection<String> candidates = new LinkedHashSet<String>();
    String text = expression.getText();
    if (expression instanceof LogtalkCallExpression) {
      final LogtalkExpression callee = ((LogtalkCallExpression)expression).getExpression();
      text = callee.getText();
    }

    if (text != null) {
      candidates.addAll(LogtalkNameSuggesterUtil.generateNames(text));
    }

    // todo: add suggestions

    final Set<String> usedNames = LogtalkRefactoringUtil.collectUsedNames(expression);
    final Collection<String> result = new ArrayList<String>();

    for (String candidate : candidates) {
      int index = 0;
      String suffix = "";
      while (usedNames.contains(candidate + suffix)) {
        suffix = Integer.toString(++index);
      }
      result.add(candidate + suffix);
    }

    return result;
  }

  protected void performIntroduceWithDialog(LogtalkIntroduceOperation operation) {
    final Project project = operation.getProject();
    if (operation.getName() == null) {
      LogtalkIntroduceDialog dialog = new LogtalkIntroduceDialog(project, myDialogTitle, operation);
      dialog.show();
      if (!dialog.isOK()) {
        return;
      }
      operation.setName(dialog.getName());
      operation.setReplaceAll(dialog.doReplaceAllOccurrences());
    }

    PsiElement declaration = performRefactoring(operation);
    if (declaration == null) {
      return;
    }
    final Editor editor = operation.getEditor();
    editor.getCaretModel().moveToOffset(declaration.getTextRange().getEndOffset());
    editor.getSelectionModel().removeSelection();
  }

  protected void performInplaceIntroduce(LogtalkIntroduceOperation operation) {
    final PsiElement statement = performRefactoring(operation);
    final LogtalkComponent target = PsiTreeUtil.findChildOfType(statement, LogtalkComponent.class);
    if (target == null) {
      return;
    }
    final List<PsiElement> occurrences = operation.getOccurrences();
    final PsiElement occurrence = LogtalkRefactoringUtil.findOccurrenceUnderCaret(occurrences, operation.getEditor());
    final PsiElement elementForCaret = occurrence != null ? occurrence : target;
    operation.getEditor().getCaretModel().moveToOffset(elementForCaret.getTextRange().getStartOffset());
    final InplaceVariableIntroducer<PsiElement> introducer =
      new LogtalkInplaceVariableIntroducer(target.getComponentName(), operation, occurrences);
    introducer.performInplaceRefactoring(new LinkedHashSet<String>(operation.getSuggestedNames()));
  }

  @Nullable
  protected PsiElement performRefactoring(LogtalkIntroduceOperation operation) {
    PsiElement declaration = createDeclaration(operation);
    if (declaration == null) {
      showCannotPerformError(operation.getProject(), operation.getEditor());
      return null;
    }

    declaration = performReplace(declaration, operation);
    declaration = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(declaration);
    return declaration;
  }

  @Nullable
  public PsiElement createDeclaration(LogtalkIntroduceOperation operation) {
    final Project project = operation.getProject();
    final LogtalkExpression initializer = operation.getInitializer();
    InitializerTextBuilder builder = new InitializerTextBuilder();
    initializer.accept(builder);
    String assignmentText = "var " + operation.getName() + " = " + builder.result() + ";";
    PsiElement anchor = operation.isReplaceAll()
                        ? findAnchor(operation.getOccurrences())
                        : findAnchor(initializer);
    return createDeclaration(project, assignmentText, anchor);
  }

  @Nullable
  protected PsiElement createDeclaration(Project project, String text, PsiElement anchor) {
    return LogtalkElementGenerator.createStatementFromText(project, text);
  }

  private PsiElement performReplace(@NotNull final PsiElement declaration, final LogtalkIntroduceOperation operation) {
    final LogtalkExpression expression = operation.getInitializer();
    final Project project = operation.getProject();
    return new WriteCommandAction<PsiElement>(project, expression.getContainingFile()) {
      protected void run(final Result<PsiElement> result) throws Throwable {
        final PsiElement createdDeclaration = addDeclaration(operation, declaration);
        result.setResult(createdDeclaration);
        if (createdDeclaration != null) {
          modifyDeclaration(createdDeclaration);
        }

        PsiElement newExpression = createExpression(project, operation.getName());

        if (operation.isReplaceAll()) {
          List<PsiElement> newOccurrences = new ArrayList<PsiElement>();
          for (PsiElement occurrence : operation.getOccurrences()) {
            final PsiElement replaced = replaceExpression(occurrence, newExpression, operation);
            if (replaced != null) {
              newOccurrences.add(replaced);
            }
          }
          operation.setOccurrences(newOccurrences);
        }
        else {
          final PsiElement replaced = replaceExpression(expression, newExpression, operation);
          operation.setOccurrences(Collections.singletonList(replaced));
        }

        postRefactoring(operation.getElement());
      }
    }.execute().getResultObject();
  }

  protected void modifyDeclaration(@NotNull PsiElement declaration) {
    final PsiElement newLineNode = PsiParserFacade.SERVICE.getInstance(declaration.getProject()).createWhiteSpaceFromText("\n");
    final PsiElement parent = declaration.getParent();
    parent.addAfter(newLineNode, declaration);
  }

  @Nullable
  protected LogtalkReference createExpression(Project project, String name) {
    return LogtalkElementGenerator.createReferenceFromText(project, name);
  }

  @Nullable
  protected PsiElement replaceExpression(PsiElement expression, PsiElement newExpression, LogtalkIntroduceOperation operation) {
    return expression.replace(newExpression);
  }


  protected void postRefactoring(PsiElement element) {
  }

  @Nullable
  public PsiElement addDeclaration(LogtalkIntroduceOperation operation, PsiElement declaration) {
    final PsiElement expression = operation.getInitializer();
    return addDeclaration(expression, declaration, operation);
  }

  @Nullable
  protected abstract PsiElement addDeclaration(@NotNull final PsiElement expression,
                                               @NotNull final PsiElement declaration,
                                               @NotNull LogtalkIntroduceOperation operation);


  private static class LogtalkInplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {
    private final LogtalkComponentName myTarget;

    public LogtalkInplaceVariableIntroducer(LogtalkComponentName target,
                                         LogtalkIntroduceOperation operation,
                                         List<PsiElement> occurrences) {
      super(target, operation.getEditor(), operation.getProject(), "Introduce Variable",
            occurrences.toArray(new PsiElement[occurrences.size()]), null);
      myTarget = target;
    }

    @Override
    protected PsiElement checkLocalScope() {
      return myTarget.getContainingFile();
    }
  }

  private static class InitializerTextBuilder extends PsiRecursiveElementVisitor {
    private final StringBuilder myResult = new StringBuilder();

    @Override
    public void visitWhiteSpace(PsiWhiteSpace space) {
      myResult.append(space.getText().replace('\n', ' '));
    }

    @Override
    public void visitElement(PsiElement element) {
      if (element.getChildren().length == 0) {
        myResult.append(element.getText());
      }
      else {
        super.visitElement(element);
      }
    }

    public String result() {
      return myResult.toString();
    }
  }
}
