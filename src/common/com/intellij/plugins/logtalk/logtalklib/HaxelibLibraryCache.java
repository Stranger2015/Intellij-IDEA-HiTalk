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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.plugins.haxe.util.LogtalkDebugTimeLog;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Manages library retrieval and caching.
 *
 * This should be instantiated once for each SDK in the project.  (Projects,
 * particularly those that keep separate versions of the libraries in
 * source control using separate branches, are not necessarily using the
 * same logtalk installation.)
 */
public final class LogtalklibLibraryCache {

  static final Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.haxelib.LogtalkLibraryManager");
  {
    LOG.setLevel(Level.DEBUG);
  }

  private final InternalCache myCache;
  private ConcurrentSkipListSet<String> knownLibraries;
  private final Sdk mySdk;


  public LogtalklibLibraryCache(@NotNull Sdk sdk) {

    List<String> installedLibs = LogtalklibUtil.getInstalledLibraryNames(sdk);

    mySdk = sdk;
    myCache = new InternalCache();
    knownLibraries = new ConcurrentSkipListSet<String>();

    for (String libName : installedLibs) {
      LogtalkLibrary lib = LogtalkLibrary.load(this, libName, mySdk);
      if (null != lib) {
        myCache.add(lib);
        knownLibraries.add(lib.getName());
      }
    }
  }

  /**
   * Get a union of all of the classpaths for the given libraries.
   *
   * @param libraryNames a set of libraries of current interest.
   * @return a (possibly empty) collection of classpaths.
   */
  @NotNull
  public LogtalkClasspath getClasspathForLogtalklibs(@Nullable List<String> libraryNames) {
    if (null == libraryNames || libraryNames.isEmpty())
      return LogtalkClasspath.EMPTY_CLASSPATH;

    LogtalkClasspath paths = new LogtalkClasspath(libraryNames.size());
    for (String libName : libraryNames) {
      LogtalkClasspath libPath = getClasspathForLogtalklib(libName);
      paths.addAll(libPath);
    }
    return paths;
  }

  /**
   * Get the classpath for a specific library.  If it does not reside in
   * the cache, it will be looked up and cached for future use.
   *
   * @param libraryName name of the library of interest.
   * @return a (possibly empty) list of classpaths known for that library.
   */
  @NotNull
  public LogtalkClasspath getClasspathForLogtalklib(String libraryName) {

    LogtalkDebugTimeLog timeLog = LogtalkDebugTimeLog.startNew("getClasspathForLibrary",
                                                         LogtalkDebugTimeLog.Since.Start);
    try {
      if (libraryIsKnown(libraryName)) {

        timeLog.stamp("Loading library:" + libraryName);

        // Try the cache first.
        LogtalkLibrary lib = myCache.get(libraryName);
        if (null != lib) {
          timeLog.stamp("Returning cached results");
          return lib.getClasspathEntries();
        }

        timeLog.stamp("Cache miss");

        // It's not in the cache, so go get it and cache the results.
        LogtalkLibrary newlib = LogtalkLibrary.load(this, libraryName, mySdk);
        myCache.add(newlib);

        timeLog.stamp("Finished loading library: " + libraryName);
        return newlib.getClasspathEntries();
      }

      timeLog.stamp("Unknown library !!!  " + libraryName + " !!! ");

      return LogtalkClasspath.EMPTY_CLASSPATH;
    }
    finally {
      timeLog.printIfTimeExceeds(2); // Short-timed logs just clutter up the ouput.
    }
  }

  @Nullable
  public LogtalkLibrary getLibrary(String name, LogtalklibSemVer requestedVersion) {
    if (libraryIsKnown(name)) {
      LogtalkLibrary lib = myCache.get(name);  // We only ever load the "current" one.
      if (null != lib && (null == requestedVersion || requestedVersion.matchesRequestedVersion(lib.getVersion()))) {
        return lib;
      }
    }
    return null;
  }

  @NotNull
  public Sdk getSdk() {
    return mySdk;
  }

  /**
   * Find a library on the haxelib path and return its complete class path.
   *
   * @param libraryName file to find.
   * @return a list of path names in the requested library, if any.
   */
  @NotNull
  public LogtalkClasspath findLogtalklibPath(@NotNull String libraryName) {
    if (! libraryIsKnown(libraryName)) {
      return LogtalkClasspath.EMPTY_CLASSPATH;
    }

    LogtalkLibrary cacheEntry = myCache.get(libraryName);
    if (cacheEntry != null) {
      return cacheEntry.getClasspathEntries();
    }

    return LogtalklibClasspathUtils.getLogtalklibLibraryPath(mySdk, libraryName);
  }

  /**
   * Retrieve the known libraries, first from the cache, then, if missing,
   * from haxelib.
   *
   * @return a collection of known libraries.
   */
  @NotNull
  private Collection<String> retrieveKnownLibraries() {
    // If we don't have the list, then load it.
    if (null == knownLibraries) {
      List<String> libs = LogtalklibClasspathUtils.getInstalledLibraries(mySdk);
      knownLibraries = new ConcurrentSkipListSet<String>(libs);
    }
    return knownLibraries;
  }

  /**
   * Tell if a given library is known to haxelib.
   *
   * @param libraryName the library of interest.  Case sensitive!
   * @return true if the library is found, false otherwise.
   */
  public boolean libraryIsKnown(String libraryName) {
    return retrieveKnownLibraries().contains(libraryName);
  }

  /**
   * Get a list of all of the libraries known to this library manager.
   * @return a (possibly empty) list of all known libraries.
   */
  public List<String> getKnownLibraries() {
    Collection<String> knownLibs = retrieveKnownLibraries();
    ArrayList<String> aryLibs = new ArrayList<String>(knownLibs.size());
    aryLibs.addAll(knownLibs);
    return aryLibs;
  }

  /**
   * Lookup a library from its path (classpath entry).
   *
   * @param path Potential path to a library.
   * @return a library matching that path.
   */
  @Nullable
  public LogtalkLibrary getLibraryByPath(String path) {
    // Looking up a library in the cache resolves to a serial search.  Instead,
    // parse the path for a library name and version, and then look it up.

    LogtalkLibraryInfo info = LogtalklibUtil.deriveLibraryInfoFromPath(mySdk, path);
    if (null != info) {
      return getLibrary(info.name, info.semver);
    }
    return null;
  }

  /**
   * A simple cache of entries.  This is used to cache the return values
   * from the haxelib command.  It should be checked before running
   * haxelib.
   */
  private final class InternalCache {
    final Hashtable<String, LogtalkLibrary> myCache;

    public InternalCache() {
      myCache = new Hashtable<String, LogtalkLibrary>();
    }

    public InternalCache(List<LogtalkLibrary> entries) {
      this();
      addAll(entries);
    }

    public void addAll(List<LogtalkLibrary> entries) {
      for (LogtalkLibrary entry : entries) {
        add(entry);
      }
    }

    public void add(LogtalkLibrary entry) {
      LogtalkLibrary oldEntry = myCache.put(entry.getName(), entry);
      if (null != oldEntry) {
        LOG.warn("Duplicating cached data for entry " + entry.getName());
      }
    }

    public void clear() {
      myCache.clear();
    }

    @Nullable
    public LogtalkLibrary get(@NotNull String name) {
      return myCache.get(name);
    }
  }


}
