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
package com.intellij.plugins.logtalk.config;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugins.logtalk.util.LogtalkModificationTracker;
import com.intellij.plugins.logtalk.util.LogtalkTrackedModifiable;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.jdom.Element;

import java.util.Arrays;
import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
@State(
  name = "LogtalkProjectSettings",
  storages = {
    @Storage(file = StoragePathMacros.PROJECT_FILE),
    @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/logtalk.xml", scheme = StorageScheme.DIRECTORY_BASED)
  }
)
public class LogtalkProjectSettings implements PersistentStateComponent<Element>, LogtalkTrackedModifiable {
  public static final String HAXE_SETTINGS = "LogtalkProjectSettings";
  public static final String DEFINES = "defines";
  private String userCompilerDefinitions = "";
  private LogtalkModificationTracker tracker = new LogtalkModificationTracker(getClass().getName());

  public Set<String> getUserCompilerDefinitionsAsSet() {
    return new THashSet<String>(Arrays.asList(getUserCompilerDefinitions()));
  }

  public static LogtalkProjectSettings getInstance(Project project) {
    return ServiceManager.getService(project, LogtalkProjectSettings.class);
  }

  public String[] getUserCompilerDefinitions() {
    // TODO: Bug here, if there are definitions that contain commas (e.g. mylib_version="2,4,3")
    return userCompilerDefinitions.split(",");
  }

  public void setUserCompilerDefinitions(String[] userCompilerDefinitions) {
    this.userCompilerDefinitions = StringUtil.join(ContainerUtil.filter(userCompilerDefinitions, new Condition<String>() {
      @Override
      public boolean value(String s) {
        return s != null && !s.isEmpty();
      }
    }), ",");
    tracker.notifyUpdated();
  }

  @Override
  public void loadState(Element state) {
    userCompilerDefinitions = state.getAttributeValue(DEFINES, "");
    tracker.notifyUpdated();
  }

  @Override
  public Element getState() {
    final Element element = new Element(HAXE_SETTINGS);
    element.setAttribute(DEFINES, userCompilerDefinitions);
    return element;
  }

  @Override
  public Stamp getStamp() {
    return tracker.getStamp();
  }

  @Override
  public boolean isModifiedSince(Stamp s) {
    return tracker.isModifiedSince(s);
  }
}
