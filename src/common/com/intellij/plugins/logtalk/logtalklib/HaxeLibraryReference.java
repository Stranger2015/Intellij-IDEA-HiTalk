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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.plugins.haxe.util.LogtalkDebugLogger;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A library wrapper used in LogtalkLibraryLists as created when adding
 * or removing libraries from the module and project library tables.
 *
 * Note that equals on this class denotes that one reference (or subclass)
 * points to the same library as another reference.  NOT that they are
 * the same reference or member-for-member identical.  Use matches() for that.
 */
public class LogtalkLibraryReference {

  private static LogtalkDebugLogger LOG = LogtalkDebugLogger.getLogger();
  static { LOG.setLevel(Level.DEBUG);} // Remove when finished debugging.

  protected final String name;
  protected final LogtalklibLibraryCache owner;
  protected final LogtalklibSemVer semver;
  protected final AtomicBoolean isManaged = new AtomicBoolean(false);

  public LogtalkLibraryReference(@NotNull Module module, @NotNull String libName, @NotNull LogtalklibSemVer semver) {
    // TODO: I don't like stuffing the "isManaged" knowledge into the name.  Find a better way.
    this(module, libName, semver, false);
  }

  public LogtalkLibraryReference(@NotNull LogtalklibLibraryCache owner, @NotNull String libName, @NotNull LogtalklibSemVer semver) {
    this(owner, libName, semver, false);
  }

  public LogtalkLibraryReference(@NotNull Module module, @NotNull String libName, @NotNull LogtalklibSemVer semver, boolean isManaged) {
    this(LogtalklibProjectUpdater.getLibraryCache(module), libName, semver, isManaged);
  }

  public LogtalkLibraryReference(@NotNull LogtalklibLibraryCache owner, @NotNull String libName, @NotNull LogtalklibSemVer semver, boolean isManaged) {
    this.name = LogtalklibNameUtil.parseLogtalklib(libName);
    this.isManaged.set(isManaged || LogtalklibNameUtil.isManagedLibrary(libName)); // NOT this.name, which has already been stripped!
    this.semver = semver;
    this.owner = owner;
  }

  public LogtalkLibraryReference clone() {
    synchronized (this) {
      return new LogtalkLibraryReference(owner, name, semver, isManaged.get());
    }
  }

  public static LogtalkLibraryReference create(@NotNull Module module, @NotNull String name) {
    return create(module.getProject(), name);
  }

  public static LogtalkLibraryReference create(@NotNull Project project, @NotNull String name) {
    LogtalklibLibraryCache owner = LogtalklibProjectUpdater.getLibraryCache(project);
    return create(owner, name);
  }

  public static LogtalkLibraryReference create(@NotNull LogtalklibLibraryCache owner, @NotNull String name) {
    if (name.isEmpty()) {
      return null;
    }

    if (name.contains(":")) {
      String[] parts = name.split(":");
      if (parts.length > 2) {
        LOG.warn("Unexpectedly encountered multiple colons in library description.");
      }
      return new LogtalkLibraryReference(owner, parts[0], LogtalklibSemVer.create(parts[1]));
    }
    return new LogtalkLibraryReference(owner, name, LogtalklibSemVer.ANY_VERSION);
  }

  /**
   * @return the library that this reference refers to.
   */
  @Nullable
  public LogtalkLibrary getLibrary() {
    return owner != null ? owner.getLibrary(name, semver) : null;
  }

  /**
   * @return the undecorated name of the library that this reference refers to.
   */
  @NotNull
  public String getName() {
    return name;
  }

  /**
   * Get the owner of the library.
   *
   * @return the Cache that owns the reference (as given at instantiation).
   */
  @NotNull
  public LogtalklibLibraryCache getOwner() {
    return owner;
  }

  /**
   * @return the semantic version number for this reference.  If this is a development library,
   *         the version may be LogtalklibSemVer.DEVELOPMENT_VERSION instead of the library's internal version number.
   */
  @NotNull
  public LogtalklibSemVer getVersion() {
    return semver;
  }

  /**
   * @return the decorated name of the library that this reference refers to.
   *         The decorated name contains information about whether this object is managed
   *         automatically.
   */
  @NotNull
  public String getPresentableName() {
    StringBuilder bld = new StringBuilder();
    bld.append(isManaged.get() ? LogtalklibNameUtil.stringifyLogtalklib(name) : name);
    bld.append(':');
    bld.append(semver.toString());
    return bld.toString();
  }

  /**
   * Determine if the library this reference points to is available. (The name and version
   * may exist, but if the lib isn't current, then it won't be available -- see 'haxelib set'.)
   *
   * @return if the library this reference points to is available.
   */
  public boolean isAvailable() {
    return getLibrary() != null;
  }

  /**
   * @return whether or not this reference is managed.
   */
  public boolean isManaged() {
    return isManaged.get();
  }

  /**
   * Mark this entry as managed.
   */
  public void markAsManagedEntry() {
    synchronized (this) {
      isManaged.set(true);
    }
  }

  public String toString() {
    return name + ":" + semver;
  }

  /**
   * Determine if this reference matches the given library, matching name and
   * semantic version.  If class library path matching is required, use
   * getLibrary().matchesIdeaLib().
   *
   * @param lib
   * @return
   */
  public boolean matchesIdeaLib(Library lib) {
    LogtalkLibraryReference ref = LogtalkLibraryReference.create(getOwner(), lib.getName());
    return this.equals(ref);
  }

  /**
   * Exact equivalence -- match every member.
   * @param o
   * @return
   */
  public boolean matches(Object o) {
    if (this == o) return true;
    if (!(o instanceof LogtalkLibraryReference)) return false;

    LogtalkLibraryReference reference = (LogtalkLibraryReference)o;

    if (!name.equals(reference.name)) return false;
    if (!owner.equals(reference.owner)) return false;
    if (!semver.equals(reference.semver)) return false;
    return isManaged.equals(reference.isManaged);
  }


  /**
   * Equals for this class is a **SOFT** equivalency.  If two references point to
   * the same library (following haxelib's rules -- see LogtalklibSemVer), they are equal.
   */
  @Override
  final public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LogtalkLibraryReference)) return false;

    LogtalkLibraryReference reference = (LogtalkLibraryReference)o;

    if (!name.equals(reference.name)) return false;
    return semver.matchesRequestedVersion(reference.semver);
  }

  @Override
  final public int hashCode() {
    int result = name.hashCode();
    // result = 31 * result + semver.hashCode();
    return result;
  }
}
