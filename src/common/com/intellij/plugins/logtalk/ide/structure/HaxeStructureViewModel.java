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
package com.intellij.plugins.logtalk.ide.structure;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.java.VisibilitySorter;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {
  public LogtalkStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor) {
    super(psiFile, editor, new LogtalkStructureViewElement(psiFile));
    withSorters(Sorter.ALPHA_SORTER, VisibilitySorter.INSTANCE);
    withSuitableClasses(LogtalkNamedComponent.class, LogtalkClass.class);
  }

  @Override
  public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
    return false;
  }

  @NotNull
  @Override
  public Filter[] getFilters() {
    return new Filter[]{ourFieldsFilter};
  }

  @Override
  public boolean isAlwaysLeaf(StructureViewTreeElement element) {
    final Object value = element.getValue();
    return value instanceof LogtalkNamedComponent && !(value instanceof LogtalkClass);
  }

  @Override
  public boolean shouldEnterElement(Object element) {
    return element instanceof LogtalkClass;
  }


  private static final Filter ourFieldsFilter = new Filter() {
    @NonNls public static final String ID = "SHOW_FIELDS";

    public boolean isVisible(TreeElement treeNode) {
      if (!(treeNode instanceof LogtalkStructureViewElement)) return true;
      final PsiElement element = ((LogtalkStructureViewElement)treeNode).getRealElement();

      if (LogtalkComponentType.typeOf(element) == LogtalkComponentType.FIELD) {
        return false;
      }

      return true;
    }

    public boolean isReverted() {
      return true;
    }

    @NotNull
    public ActionPresentation getPresentation() {
      return new ActionPresentationData(
        IdeBundle.message("action.structureview.show.fields"),
        null,
        PlatformIcons.FIELD_ICON
      );
    }

    @NotNull
    public String getName() {
      return ID;
    }
  };
}
