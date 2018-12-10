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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.plugins.logtalk.LogtalkFileType;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkComponent;
import com.intellij.plugins.logtalk.util.LogtalkResolveUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkComponentIndex extends FileBasedIndexExtension<String, LogtalkClassInfo> {
  public static final ID<String, LogtalkClassInfo> HAXE_COMPONENT_INDEX = ID.create("LogtalkComponentIndex");
  private static final int INDEX_VERSION = LogtalkIndexUtil.BASE_INDEX_VERSION + 6;
  private final DataIndexer<String, LogtalkClassInfo, FileContent> myIndexer = new MyDataIndexer();
  private final DataExternalizer<LogtalkClassInfo> myExternalizer = new LogtalkClassInfoExternalizer();

  @NotNull
  @Override
  public ID<String, LogtalkClassInfo> getName() {
    return HAXE_COMPONENT_INDEX;
  }

  @Override
  public int getVersion() {
    return INDEX_VERSION;
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }

  @Override
  public KeyDescriptor<String> getKeyDescriptor() {
    return new EnumeratorStringDescriptor();
  }

  @Override
  public DataExternalizer<LogtalkClassInfo> getValueExternalizer() {
    return myExternalizer;
  }

  @Override
  public FileBasedIndex.InputFilter getInputFilter() {
    return LogtalkSdkInputFilter.INSTANCE;
  }

  @NotNull
  @Override
  public DataIndexer<String, LogtalkClassInfo, FileContent> getIndexer() {
    return myIndexer;
  }

  public static List<LogtalkComponent> getItemsByName(String name, Project project, GlobalSearchScope searchScope) {
    LogtalkIndexUtil.warnIfDumbMode(project);
    Collection<VirtualFile> files =
      FileBasedIndex.getInstance().getContainingFiles(HAXE_COMPONENT_INDEX, name, searchScope);
    final List<LogtalkComponent> result = new ArrayList<>();
    for (VirtualFile vFile : files) {
      PsiFile file = PsiManager.getInstance(project).findFile(vFile);
      if (file == null || file.getFileType() != LogtalkFileType.HAXE_FILE_TYPE) {
        continue;
      }
      final LogtalkComponent component = LogtalkResolveUtil.findComponentDeclaration(file, name);
      if (component != null) {
        result.add(component);
      }
    }
    return result;
  }

  public static void processAll(Project project, Processor<Pair<String, LogtalkClassInfo>> processor, GlobalSearchScope scope) {
    LogtalkIndexUtil.warnIfDumbMode(project);
    final Collection<String> keys = getNames(project);
    for (String key : keys) {
      final List<LogtalkClassInfo> values = FileBasedIndex.getInstance().getValues(HAXE_COMPONENT_INDEX, key, scope);
      for (LogtalkClassInfo value : values) {
        final Pair<String, LogtalkClassInfo> pair = Pair.create(key, value);
        if (!processor.process(pair)) {
          return;
        }
      }
    }
  }

  public static Collection<String> getNames(Project project) {
    LogtalkIndexUtil.warnIfDumbMode(project);
    return FileBasedIndex.getInstance().getAllKeys(HAXE_COMPONENT_INDEX, project);
  }

  private static class MyDataIndexer implements DataIndexer<String, LogtalkClassInfo, FileContent> {
    @Override
    @NotNull
    public Map<String, LogtalkClassInfo> map(final FileContent inputData) {
      final PsiFile psiFile = inputData.getPsiFile();
      final List<LogtalkClass> classes = LogtalkResolveUtil.findComponentDeclarations(psiFile);
      if (classes.isEmpty()) {
        return Collections.emptyMap();
      }
      final Map<String, LogtalkClassInfo> result = new THashMap<String, LogtalkClassInfo>(classes.size());
      for (LogtalkClass haxeClass : classes) {
        if (haxeClass.getName() == null) {
          continue;
        }
        final Pair<String, String> packageAndName = LogtalkResolveUtil.splitQName(haxeClass.getQualifiedName());
        final LogtalkClassInfo info = new LogtalkClassInfo(packageAndName.getFirst(), LogtalkComponentType.typeOf(haxeClass));
        result.put(packageAndName.getSecond(), info);
      }
      return result;
    }
  }
}
