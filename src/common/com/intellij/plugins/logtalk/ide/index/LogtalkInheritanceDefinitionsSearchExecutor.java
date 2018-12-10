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
package com.intellij.plugins.logtalk.ide.index;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkNamedComponent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.DefinitionsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkInheritanceDefinitionsSearchExecutor extends QueryExecutorBase<PsiElement, PsiElement> {

  public LogtalkInheritanceDefinitionsSearchExecutor() {
    super(true); // Wrap in a read action.
  }

  public static List<LogtalkClass> getItemsByQName(final LogtalkClass haxeClass) {
    final List<LogtalkClass> result = new ArrayList<LogtalkClass>();
    DefinitionsSearch.search(haxeClass).forEach(new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement element) {
        if (element instanceof LogtalkClass) {
          result.add((LogtalkClass)element);
        }
        return true;
      }
    });
    return result;
  }

  @Override
  public void processQuery(@NotNull PsiElement queryParameters, @NotNull Processor<PsiElement> consumer) {
    processQueryInternal(queryParameters, consumer);
  }

  private boolean processQueryInternal(@NotNull final PsiElement queryParameters, @NotNull final Processor<PsiElement> consumer) {
    final PsiElement queryParametersParent = queryParameters.getParent();
    LogtalkNamedComponent haxeNamedComponent;
    if (queryParameters instanceof LogtalkClass) {
      haxeNamedComponent = (LogtalkClass)queryParameters;
    }
    else if (queryParametersParent instanceof LogtalkNamedComponent && queryParameters instanceof LogtalkComponentName) {
      haxeNamedComponent = (LogtalkNamedComponent)queryParametersParent;
    }
    else {
      return true;
    }
    if (haxeNamedComponent instanceof LogtalkClass) {
      processInheritors(((LogtalkClass)haxeNamedComponent).getQualifiedName(), queryParameters, consumer);
    }
    else if (LogtalkComponentType.typeOf(haxeNamedComponent) == LogtalkComponentType.METHOD ||
             LogtalkComponentType.typeOf(haxeNamedComponent) == LogtalkComponentType.FIELD) {
      final String nameToFind = haxeNamedComponent.getName();
      if (nameToFind == null) return true;

      LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(haxeNamedComponent, LogtalkClass.class);
      assert haxeClass != null;

      processInheritors(haxeClass.getQualifiedName(), queryParameters, new Processor<PsiElement>() {
        @Override
        public boolean process(PsiElement element) {
          for (LogtalkNamedComponent subLogtalkNamedComponent : LogtalkResolveUtil.getNamedSubComponents((LogtalkClass)element)) {
            if (nameToFind.equals(subLogtalkNamedComponent.getName())) {
              consumer.process(subLogtalkNamedComponent);
            }
          }
          return true;
        }
      });
    }
    return true;

  }

  private boolean processInheritors(final String qName, final PsiElement context, final Processor<PsiElement> consumer) {
    final Set<String> namesSet = new THashSet<String>();
    final LinkedList<String> namesQueue = new LinkedList<String>();
    namesQueue.add(qName);
    final Project project = context.getProject();
    final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    while (!namesQueue.isEmpty()) {
      final String name = namesQueue.pollFirst();
      if (!namesSet.add(name)) {
        continue;
      }
      List<List<LogtalkClassInfo>> files = FileBasedIndex.getInstance().getValues(LogtalkInheritanceIndex.HAXE_INHERITANCE_INDEX, name, scope);
      files.addAll(FileBasedIndex.getInstance().getValues(LogtalkTypeDefInheritanceIndex.HAXE_TYPEDEF_INHERITANCE_INDEX, name, scope));
      for (List<LogtalkClassInfo> subClassInfoList : files) {
        for (LogtalkClassInfo subClassInfo : subClassInfoList) {
          final LogtalkClass subClass = LogtalkResolveUtil.findClassByQName(subClassInfo.getValue(), context.getManager(), scope);
          if (subClass != null) {
            if (!consumer.process(subClass)) {
              return true;
            }
            namesQueue.add(subClass.getQualifiedName());
          }
        }
      }
    }
    return true;
  }
}
