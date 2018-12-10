/*
 * Copyright 2017 Eric Bishton
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
import com.intellij.plugins.haxe.LogtalkBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LogtalkLibraryDependency extends LogtalkLibraryReference {
  final private LogtalkLibraryList reliants;


  public LogtalkLibraryDependency(@NotNull Module module, @NotNull String name, @Nullable String semver) {
    this(module, name, semver, null);
  }

  public LogtalkLibraryDependency(@NotNull Module module, @NotNull String name, @Nullable String semver, @Nullable LogtalkLibrary reliant) {
    this(LogtalklibProjectUpdater.getLibraryCache(module), name, semver, reliant);
  }

  public LogtalkLibraryDependency(@NotNull LogtalklibLibraryCache owner, @NotNull String name, @Nullable String semver, @Nullable LogtalkLibrary reliant) {
    super(owner, name, LogtalklibSemVer.create(semver), false);
    reliants = new LogtalkLibraryList(owner.getSdk());
    if (null != reliant) {
      this.reliants.add(reliant.createReference());
    }
  }

  public LogtalkLibraryDependency(@NotNull LogtalklibLibraryCache owner, @NotNull String name, @Nullable String semver, @NotNull LogtalkLibraryList reliants) {
    this(owner, name, semver, (LogtalkLibrary)null);
    this.reliants.addAll(reliants);
  }

  public LogtalkLibraryDependency clone() {
    synchronized (this) {
      return new LogtalkLibraryDependency(owner, name, semver.toString(), reliants);
    }
  }

  @Override
  public String toString() {
    return computePresentableName();
  }

  // Note: This was intended to override getPresentableName(), but doing so
  //       makes it difficult to locate the library in the project list.
  @NotNull
  public String computePresentableName() {
    String name = super.getPresentableName();
    List<String> relnames = null;

    synchronized (this) {
      if (!reliants.isEmpty()) {
        final int size = reliants.size(); // Slow call!! Don't repeat it.
        final List<String> namelist = new ArrayList<String>(size);
        reliants.iterate(new LogtalkLibraryList.Lambda() {
          @Override
          public boolean processEntry(LogtalkLibraryReference entry) {
            namelist.add(entry.getName());
            return true;
          }
        });
        relnames = namelist;
      }
    }

    if (null != relnames && !relnames.isEmpty()) {
      final StringBuilder builder = new StringBuilder(name);
      builder.append(' ');
      builder.append(LogtalkBundle.message("haxelib.dependency.list.prefix"));

      int size = relnames.size();
      for (int i = 0; i < size; ++i) {
        builder.append(relnames.get(i));
        if (i < size - 1) {
          builder.append(LogtalkBundle.message("haxelib.dependency.list.separator"));
        }
      }
      name = builder.toString();
    }

    return name;
  }

  public LogtalkLibraryList getReliants() {
    return reliants;
  }

  public void addReliant(LogtalkLibrary reliant) {
    LogtalkLibraryReference newRef = reliant.createReference();
    if (null != reliant && !reliants.contains(newRef)) {
      reliants.add(newRef);
    }

  }

  public void addReliant(LogtalkLibraryReference reliant) {
    if (null != reliant && !reliants.contains(reliant)) {
      reliants.add(reliant.clone());
    }
  }

  /**
   * Generate a key that matches dependencies.
   * @return
   */
  public String getKey() {
    return getName();
  }

  /**
   * Full member-by-member equivalency.
   * @param o
   * @return
   */
  @Override
  public boolean matches(Object o) {
    if (this == o) return true;
    if (!(o instanceof LogtalkLibraryDependency)) return false;
    if (!super.matches(o)) return false;

    LogtalkLibraryDependency that = (LogtalkLibraryDependency)o;

    return reliants != null ? reliants.equals(that.reliants) : that.reliants == null;
  }
}
