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
package com.intellij.plugins.logtalk.ide.hierarchy.type;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikanthg on 1/5/15.
 */
public final class LogtalkSupertypesHierarchyTreeStructure extends HierarchyTreeStructure {

  public LogtalkSupertypesHierarchyTreeStructure(final Project project, final PsiClass aClass) {
    super(project, new LogtalkTypeHierarchyNodeDescriptor(project, null, aClass, true));
    setBaseElement(myBaseDescriptor); // to set myRoot
  }

  protected final Object[] buildChildren(final HierarchyNodeDescriptor descriptor) {
    final PsiClass theLogtalkClass = ((LogtalkTypeHierarchyNodeDescriptor) descriptor).getLogtalkClass();
    if (null == theLogtalkClass) return ArrayUtil.EMPTY_OBJECT_ARRAY;
    final PsiClass[] supers = theLogtalkClass.getSupers();
    final List<LogtalkTypeHierarchyNodeDescriptor> descriptors = new ArrayList<LogtalkTypeHierarchyNodeDescriptor>();
    for (PsiClass aSuper : supers) {
        descriptors.add(new LogtalkTypeHierarchyNodeDescriptor(myProject, descriptor, aSuper, false));
    }
    return descriptors.toArray(new LogtalkTypeHierarchyNodeDescriptor[descriptors.size()]);
  }
}