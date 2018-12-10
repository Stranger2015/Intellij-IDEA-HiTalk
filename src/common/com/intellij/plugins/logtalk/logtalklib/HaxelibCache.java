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
package com.intellij.plugins.logtalk.logtalklib;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.plugins.haxe.ide.module.LogtalkModuleType;

import java.util.Collections;
import java.util.List;

/**
 * Created by as3boyan on 15.11.14.
 *
 * TODO: EBatTiVo -- this class has to go away to be replaced by calls to LogtalklibLibraryManager.
 */
public class LogtalklibCache {
  protected static LogtalklibCache instance = null;

  public List<String> getAvailableLogtalklibs() {
    return availableLogtalklibs;
  }

  public List<String> getLocalLogtalklibs() {
    return localLogtalklibs;
  }

  protected static List<String> availableLogtalklibs = Collections.emptyList();
  protected static List<String> localLogtalklibs = Collections.emptyList();

  private LogtalklibCache() {
    load();
  }

  public static LogtalklibCache getInstance() {
    if (instance == null) {
      instance = new LogtalklibCache();
    }

    return instance;
  }

  private void load() {
    Module haxeModule = getLogtalkModule();

    if (haxeModule != null) {
      Sdk sdk = LogtalklibSdkUtils.lookupSdk(haxeModule);
      LogtalklibLibraryCacheManager sdkManager = LogtalklibProjectUpdater.getInstance().getLibraryCacheManager(haxeModule);
      LogtalklibLibraryCache libManager = sdkManager == null ? null : sdkManager.getLibraryManager(haxeModule);
      localLogtalklibs = libManager != null
                    ? libManager.getKnownLibraries()  // Use the cache
                    : LogtalklibClasspathUtils.getInstalledLibraries(sdk); // the slow way

      availableLogtalklibs = LogtalklibClasspathUtils.getAvailableLibrariesMatching(sdk, "\"\"");  // Empty string means all of them.
      availableLogtalklibs.removeAll(localLogtalklibs);
    }
  }

  public static Module getLogtalkModule() {
    Module haxeModule = null;
    Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
    for (Project project : openProjects) {
      for (Module module : ModuleUtil.getModulesOfType(project, LogtalkModuleType.getInstance())) {
        haxeModule = module;
        break;
      }
    }
    return haxeModule;
  }
}
