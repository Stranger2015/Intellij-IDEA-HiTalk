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

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugins.haxe.util.LogtalkDebugLogger;
import com.intellij.plugins.logtalk.util.LogtalkFileUtil;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;

public class LogtalkLibrary {

  private static LogtalkDebugLogger LOG = LogtalkDebugLogger.getLogger();
  static {LOG.setLevel(Level.DEBUG);}  // Remove when finished debugging.

  private static String CURRENT_DIR = ".";

  final private LogtalklibLibraryCache myCache;
  final private String myName;
  final private String myLibraryRoot;
  final private String myRelativeClasspath;
  final private LogtalklibMetadata myMetadata;
  final private LogtalklibSemVer mySemVer;
  // TODO: Add the extraParams.hxml data here.  Use the hxml parser; see LimeUtil.getLimeProjectModel() as an example.


  private LogtalkLibrary(@NotNull String name, @NotNull VirtualFile libraryRoot, @NotNull LogtalklibLibraryCache owner) {
    myCache = owner;
    myLibraryRoot = libraryRoot.getUrl();

    myMetadata = LogtalklibMetadata.load(libraryRoot);
    LogtalkLibraryInfo pathInfo = LogtalklibUtil.deriveLibraryInfoFromPath(owner.getSdk(), libraryRoot.getPath());

    String mdname = myMetadata.getName();
    if (null != mdname && !mdname.isEmpty()) {
      myName = mdname;
    } else if (!name.isEmpty()) {
      myName = name;
    } else {
      myName = pathInfo == null ? "" : pathInfo.getName();
    }

    LogtalklibSemVer semVer = LogtalklibSemVer.create(myMetadata.getVersion());
    if (LogtalklibSemVer.ZERO_VERSION == semVer && pathInfo != null) {
      semVer = pathInfo.getVersion();
    }
    mySemVer = semVer;

    String cp = myMetadata.getClasspath();
    if ((null == cp || cp.isEmpty()) && pathInfo != null) {
        cp = pathInfo.getClasspath();
    }
    if (null != cp && !cp.isEmpty()) {
      myRelativeClasspath = cp;
    } else {
      myRelativeClasspath = CURRENT_DIR;
    }
  }

  /**
   * Get the list of libraries that this library depends upon (but not /their/ dependents).
   *
   * Private because it's only used by collectDependents.
   * Returns a List instead of a LogtalkLibraryList to keep synchronized access down.
   */
  @NotNull
  private List<LogtalkLibraryDependency> getDirectDependents() {
    List<LogtalklibMetadata.Dependency> mdDependencies = myMetadata.getDependencies();
    if (null == mdDependencies || mdDependencies.isEmpty()) {
      return Collections.emptyList();
    }
    List<LogtalkLibraryDependency> dependencies = new ArrayList<LogtalkLibraryDependency>(mdDependencies.size());
    for (LogtalklibMetadata.Dependency md : mdDependencies) {
      LogtalkLibraryDependency newdep = new LogtalkLibraryDependency(myCache, md.getName(), md.getVersion(), this);
      dependencies.add(newdep);
    }
    return dependencies;
  }

  /**
   * Get all dependent libraries in search order.
   */
  @NotNull
  public LogtalkLibraryList collectDependents() {
    LinkedHashMap<String, LogtalkLibraryDependency> collection = new LinkedHashMap<String, LogtalkLibraryDependency>();
    collectDependentsInternal(collection);
    LogtalkLibraryList list = new LogtalkLibraryList(myCache.getSdk());
    for (LogtalkLibraryDependency dep : collection.values()) {
      list.add(dep);
    }
    return list;
  }

  private void collectDependentsInternal(/*modifies*/ final @NotNull LinkedHashMap<String, LogtalkLibraryDependency> collection) {
    List<LogtalkLibraryDependency> dependencies = getDirectDependents();

    for (LogtalkLibraryDependency dependency : dependencies) {
      if (!collection.containsKey(dependency.getKey())) { // Don't go down the same path again...
        // TODO: Deal with version mismatches here.  Add multiple versions, but don't add a specific version if the latest version is equal to it.
        collection.put(dependency.getKey(), dependency);
        LogtalkLibrary depLib = dependency.getLibrary();
        if (null != depLib) {
          depLib.collectDependentsInternal(collection);
        } // TODO: Else mark dependency unfulfilled somehow??
      } else {
        LogtalkLibraryDependency contained = collection.get(dependency.getKey());
        LOG.assertLog(contained != null, "Couldn't get a contained object.");
        if (contained != null) {
          contained.addReliant(dependency);
        }
      }
    }
  }

  /**
   * Get the internal name of the library.
   */
  @NotNull
  public String getName() {
    return myName;
  }

  /**
   * Get the display name of the library.
   */
  @NotNull
  public String getPresentableName() {
    // TODO: Figure out what extra decorations we might need, like the version, 'dependency of', etc.
    return getName();
  }

  @Nullable
  public LogtalkClasspath getClasspathEntries() {
    LogtalkClasspath cp = new LogtalkClasspath();
    cp.add(getSourceRoot());
    return cp;
  }

  @NotNull
  public LogtalkClasspathEntry getSourceRoot() {
    if (CURRENT_DIR == myRelativeClasspath) {
      return getLibraryRoot();
    }
    return new LogtalkClasspathEntry(myName, LogtalkFileUtil.joinPath(myLibraryRoot, myRelativeClasspath));
  }

  @NotNull
  public LogtalkClasspathEntry getLibraryRoot() {
    return new LogtalkClasspathEntry(myName, myLibraryRoot);
  }

  @NotNull
  public LogtalklibSemVer getVersion() {
    return mySemVer;
  }

  /**
   * Load a library from disk.  This *DOES NOT* place the library into the library manager.
   *
   * @param libName - name of the library (as haxelib understands it) to load.
   * @return the loaded LogtalkLibrary of the given name; null if not found.
   */
  @Nullable
  public static LogtalkLibrary load(LogtalklibLibraryCache owner, String libName, Sdk sdk) {
    // Ask haxelib for the path to this library.
    VirtualFile libraryRoot = LogtalklibUtil.getLibraryRoot(sdk, libName);
    if (null == libraryRoot) {
      // XXX: This case might occur if the library is not managed by haxelib, but then
      //      that should be a classpath, not a lib.
      return null;
    }

    try {
      return new LogtalkLibrary(libName, libraryRoot, owner);
    } catch (InvalidParameterException e) {
      ; // libName must not have been an url
    }
    return null;
  }

  /**
   * Create a new reference for this library.
   * @param isManaged whether or not this reference is a "managed reference".
   */
  @NotNull
  public LogtalkLibraryReference createReference(boolean isManaged) {
    return new LogtalkLibraryReference(myCache, myName, mySemVer, isManaged);
  }

  /**
   * Create a new unmanaged reference for this library.
   */
  @NotNull
  public LogtalkLibraryReference createReference() {
    return new LogtalkLibraryReference(myCache, myName, mySemVer);
  }

  @NotNull
  public LogtalkLibraryReference createReference(LogtalklibSemVer override) {
    return new LogtalkLibraryReference(myCache, myName, override);
  }

  /**
   * Test whether this library is effectively the same as a Library appearing
   * in IDEA's library tables.
   *
   * @param lib - Library to test.
   * @return true if this library uses the same sources as the IDEA library; false otherwise.
   */
  public boolean matchesIdeaLib(Library lib) {
    if (null == lib) {
      return false;
    }

    LogtalkClasspath cp = getClasspathEntries();
    VirtualFile[] sources = lib.getFiles(OrderRootType.SOURCES);
    for (VirtualFile file : sources) {
      if (!cp.containsUrl(file.getUrl())) {
        return false;
      }
    }
    return cp.size() == sources.length;
  }
}
