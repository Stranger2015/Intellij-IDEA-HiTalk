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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * Created by srikanthg on 10/23/14.
 */
public class LogtalkTypeHierarchyTreeStructure extends LogtalkSubtypesHierarchyTreeStructure {

  private static final Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.ide.hierarchy.type.LogtalkTypeHierarchyTreeStructure");

  public LogtalkTypeHierarchyTreeStructure(final Project project, final PsiClass aClass) {
    super(project, buildHierarchyElement(project, aClass));
    setBaseElement(myBaseDescriptor); //to set myRoot
  }

  private static LogtalkTypeHierarchyNodeDescriptor buildHierarchyElement(final Project project, final PsiClass aClass) {
    LogtalkTypeHierarchyNodeDescriptor descriptor = null;
    final PsiClass[] superTypes = getSuperTypesAsArray(aClass);
    for(int i = superTypes.length - 1; i >= 0; i--){
      final LogtalkTypeHierarchyNodeDescriptor newDescriptor = new LogtalkTypeHierarchyNodeDescriptor(project, descriptor, superTypes[i], false);
      if (descriptor != null){
        descriptor.setCachedChildren(new LogtalkTypeHierarchyNodeDescriptor[] {newDescriptor});
      }
      descriptor = newDescriptor;
    }
    final LogtalkTypeHierarchyNodeDescriptor newDescriptor = new LogtalkTypeHierarchyNodeDescriptor(project, descriptor, aClass, true);
    if (descriptor != null) {
      descriptor.setCachedChildren(new LogtalkTypeHierarchyNodeDescriptor[] {newDescriptor});
    }
    return newDescriptor;
  }

}
