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
package com.intellij.plugins.logtalk.ide.hierarchy;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.SmartElementDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.logtalk.build.FieldWrapper;
import com.intellij.plugins.logtalk.build.IdeaTarget;
import com.intellij.plugins.logtalk.build.MethodWrapper;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ebishton on 1/25/16.
 */
public class LogtalkHierarchyNodeDescriptor extends HierarchyNodeDescriptor {

  // Wrapper fields are for compatibility.  Can go away when v14 is dropped in the future.
  private static FieldWrapper<PsiElement> myElementFieldWrapper =
    IdeaTarget.IS_VERSION_15_0_COMPATIBLE
    ? null
    : new FieldWrapper<>(SmartElementDescriptor.class, "myElement");  // One of our superclasses.
  private static MethodWrapper<PsiElement> getPsiElementWrapper =
    IdeaTarget.IS_VERSION_15_0_COMPATIBLE
    ? new MethodWrapper<>(LogtalkHierarchyNodeDescriptor.class, "getPsiElement", (Class[])null)
    : null;

  public LogtalkHierarchyNodeDescriptor(@NotNull Project project,
                                     NodeDescriptor parentDescriptor,
                                     @NotNull PsiElement element, boolean isBase) {
    super(project, parentDescriptor, element, isBase);
  }

  @Nullable
  public PsiElement getMyPsiElement() {
    if (IdeaTarget.IS_VERSION_15_COMPATIBLE) {
      return getPsiElementWrapper.invoke(this, (Object[])null);
    } else {
      return myElementFieldWrapper.get(this);
    }
  }

  @Nullable
  public final LogtalkClass getLogtalkClass() {
    PsiElement myElement = getMyPsiElement();
    return (myElement instanceof LogtalkClass) ? (LogtalkClass) myElement : null;
  }

  public final boolean isValid() {
    final LogtalkClass lgtPsiClass = getLogtalkClass();
    return lgtPsiClass != null && lgtPsiClass.isValid();
  }

}
