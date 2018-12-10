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
import com.intellij.plugins.logtalk.ide.index.LogtalkInheritanceDefinitionsSearchExecutor;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiModifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikanthg on 10/23/14.
 */
public class LogtalkSubtypesHierarchyTreeStructure extends HierarchyTreeStructure {

  protected LogtalkSubtypesHierarchyTreeStructure(final Project project, final LogtalkTypeHierarchyNodeDescriptor descriptor) {
    super(project, descriptor);
  }

  public LogtalkSubtypesHierarchyTreeStructure(final Project project, final PsiClass psiClass) {
    super(project, new LogtalkTypeHierarchyNodeDescriptor(project, null, psiClass, true));
  }

  @NotNull
  protected final Object[] buildChildren(@NotNull final HierarchyNodeDescriptor descriptor) {

    final LogtalkClass theLogtalkClass = ((LogtalkTypeHierarchyNodeDescriptor) descriptor).getLogtalkClass();
    if (null == theLogtalkClass) return ArrayUtil.EMPTY_OBJECT_ARRAY;

    if (theLogtalkClass instanceof LogtalkAnonymousType) return ArrayUtil.EMPTY_OBJECT_ARRAY;
    if (theLogtalkClass.hasModifierProperty(LogtalkPsiModifier.FINAL)) return ArrayUtil.EMPTY_OBJECT_ARRAY;

    final PsiFile inClassPsiFile = theLogtalkClass.getContainingFile();

    List<PsiClass> subTypeList = new ArrayList<PsiClass>(); // add the sub-types to this list, as they are found

    // ----
    // search current file for sub-types that extend/implement from this class/type
    //
    final LogtalkClass[] allLogtalkClassesInFile = PsiTreeUtil.getChildrenOfType(inClassPsiFile, LogtalkClass.class);
    for (LogtalkClass aClassInFile : allLogtalkClassesInFile) {
      if (isThisTypeASubTypeOfTheSuperType(aClassInFile, theLogtalkClass)) {
        subTypeList.add(aClassInFile);
      }
    }

    // if private class, scope ends there
    if (theLogtalkClass.hasModifierProperty(LogtalkPsiModifier.PRIVATE)) { // XXX: how about @:allow occurrences?
      return typeListToObjArray(((LogtalkTypeHierarchyNodeDescriptor) descriptor), subTypeList);
    }

    // Get the list of subtypes from the file-based indices.  Stub-based would
    // be faster, but we'll have to re-parent all of the PsiClass sub-classes.
    subTypeList.addAll(getSubTypes(theLogtalkClass));

    return typeListToObjArray(((LogtalkTypeHierarchyNodeDescriptor) descriptor), subTypeList);
  }

  @NotNull
  private final Object[] typeListToObjArray(@NotNull final LogtalkTypeHierarchyNodeDescriptor descriptor, @NotNull final List<PsiClass> classes) {
    final int size = classes.size();
    if (size > 0) {
      final List<LogtalkTypeHierarchyNodeDescriptor> descriptors = new ArrayList<LogtalkTypeHierarchyNodeDescriptor>(size);
      for (PsiClass aClass : classes) {
        descriptors.add(new LogtalkTypeHierarchyNodeDescriptor(myProject, descriptor, aClass, false));
      }
      return descriptors.toArray(new LogtalkTypeHierarchyNodeDescriptor[descriptors.size()]);
    }
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  private static boolean isThisTypeASubTypeOfTheSuperType(PsiClass thisType, PsiClass theSuperType) {
    if (!thisType.isValid()) return false;
    final String tcfqn = thisType.getQualifiedName();
    final String pscfqn = theSuperType.getQualifiedName();
    if (pscfqn.equals(tcfqn)) return false; // it's the same class in LHS & RHS
    final ArrayList<PsiClass> allSuperTypes = getSuperTypesAsList(thisType);
    for (PsiClass aSuperType : allSuperTypes) {
      if (pscfqn.equals(aSuperType.getQualifiedName())) {
        return true;
      }
    }
    return false;
  }

  protected static PsiClass[] getSuperTypesAsArray(PsiClass theClass) {
    if (!theClass.isValid()) return PsiClass.EMPTY_ARRAY;
    final ArrayList<PsiClass> allSuperClasses = getSuperTypesAsList(theClass);
    return allSuperClasses.toArray(new PsiClass[allSuperClasses.size()]);
  }

  private static ArrayList<PsiClass> getSuperTypesAsList(PsiClass theClass) {
    final ArrayList<PsiClass> allSuperClasses = new ArrayList<PsiClass>();
    while (true) {
      final PsiClass aClass1 = theClass;
      final PsiClass[] superTypes = aClass1.getSupers();
      PsiClass superType = null;
      for (int i = 0; i < superTypes.length; i++) {
        final PsiClass type = superTypes[i];
        if (!type.isInterface()) {
          superType = type;
          break;
        }
      }
      if (superType == null) break;
      if (allSuperClasses.contains(superType)) break;
      allSuperClasses.add(superType);
      theClass = superType;
    }
    return allSuperClasses;
  }

  private static List<LogtalkClass> getSubTypes(LogtalkClass theClass) {
    final List<LogtalkClass> subClasses = LogtalkInheritanceDefinitionsSearchExecutor.getItemsByQName(theClass);
    return subClasses;
  }
}
