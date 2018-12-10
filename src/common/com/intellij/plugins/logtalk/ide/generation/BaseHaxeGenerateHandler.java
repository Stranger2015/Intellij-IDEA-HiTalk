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
package com.intellij.plugins.logtalk.ide.generation;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.ide.util.MemberChooser;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.ide.LogtalkNamedElementNode;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkClassDeclaration;
import com.intellij.plugins.haxe.lang.psi.LogtalkFile;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.logtalk.ide.LogtalkNamedElementNode;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public abstract class BaseLogtalkGenerateHandler implements LanguageCodeInsightActionHandler {
  @Override
  public boolean isValidFor(Editor editor, PsiFile file) {
    return file instanceof LogtalkFile;
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    if (!FileModificationService.getInstance().prepareFileForWrite(file)) return;
    final LogtalkClass haxeClass =
      PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), LogtalkClassDeclaration.class);
    if (haxeClass == null) return;

    final List<LogtalkNamedComponent> candidates = new ArrayList<LogtalkNamedComponent>();
    collectCandidates(haxeClass, candidates);

    List<LogtalkNamedElementNode> selectedElements = Collections.emptyList();
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      selectedElements = ContainerUtil.map(candidates, new Function<LogtalkNamedComponent, LogtalkNamedElementNode>() {
        @Override
        public LogtalkNamedElementNode fun(LogtalkNamedComponent namedComponent) {
          return new LogtalkNamedElementNode(namedComponent);
        }
      });
    }
    else if (!candidates.isEmpty()) {
      final MemberChooser<LogtalkNamedElementNode> chooser =
        createMemberChooserDialog(project, haxeClass, candidates, getTitle());
      chooser.show();
      selectedElements = chooser.getSelectedElements();
    }

    final BaseCreateMethodsFix createMethodsFix = createFix(haxeClass);
    doInvoke(project, editor, file, selectedElements, createMethodsFix);
  }

  protected void doInvoke(final Project project,
                          final Editor editor,
                          final PsiFile file,
                          final Collection<LogtalkNamedElementNode> selectedElements,
                          final BaseCreateMethodsFix createMethodsFix) {
    Runnable runnable = new Runnable() {
      public void run() {
        createMethodsFix.addElementsToProcessFrom(selectedElements);
        createMethodsFix.beforeInvoke(project, editor, file);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          public void run() {
            try {
              createMethodsFix.invoke(project, editor, file);
            }
            catch (IncorrectOperationException ex) {
              Logger.getInstance(getClass().getName()).error(ex);
            }
          }
        });
      }
    };

    if (CommandProcessor.getInstance().getCurrentCommand() == null) {
      CommandProcessor.getInstance().executeCommand(project, runnable, getClass().getName(), null);
    }
    else {
      runnable.run();
    }
  }

  protected abstract BaseCreateMethodsFix createFix(LogtalkClass haxeClass);

  protected abstract String getTitle();

  abstract void collectCandidates(LogtalkClass aClass, List<LogtalkNamedComponent> candidates);

  @Nullable
  protected JComponent getOptionsComponent(LogtalkClass jsClass, final Collection<LogtalkNamedComponent> candidates) {
    return null;
  }

  @Override
  public boolean startInWriteAction() {
    return true;
  }

  protected MemberChooser<LogtalkNamedElementNode> createMemberChooserDialog(final Project project,
                                                                          final LogtalkClass haxeClass,
                                                                          final Collection<LogtalkNamedComponent> candidates,
                                                                          String title) {
    final MemberChooser<LogtalkNamedElementNode> chooser = new MemberChooser<LogtalkNamedElementNode>(
      ContainerUtil.map(candidates, new Function<LogtalkNamedComponent, LogtalkNamedElementNode>() {
        @Override
        public LogtalkNamedElementNode fun(LogtalkNamedComponent namedComponent) {
          return new LogtalkNamedElementNode(namedComponent);
        }
      }).toArray(new LogtalkNamedElementNode[candidates.size()]), false, true, project, false) {

      protected void init() {
        super.init();
        myTree.addTreeSelectionListener(new TreeSelectionListener() {
          public void valueChanged(final TreeSelectionEvent e) {
            setOKActionEnabled(myTree.getSelectionCount() > 0);
          }
        });
      }

      protected JComponent createCenterPanel() {
        final JComponent superComponent = super.createCenterPanel();
        final JComponent optionsComponent = getOptionsComponent(haxeClass, candidates);
        if (optionsComponent == null) {
          return superComponent;
        }
        else {
          final JPanel panel = new JPanel(new BorderLayout());
          panel.add(superComponent, BorderLayout.CENTER);
          panel.add(optionsComponent, BorderLayout.SOUTH);
          return panel;
        }
      }
    };

    chooser.setTitle(title);
    chooser.setCopyJavadocVisible(false);
    return chooser;
  }
}
