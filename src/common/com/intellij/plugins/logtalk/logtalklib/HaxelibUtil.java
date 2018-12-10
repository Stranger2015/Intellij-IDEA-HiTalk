/*
 * Copyright 2017-2018 Eric Bishton
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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.ModuleRootManagerImpl;
import com.intellij.openapi.roots.impl.RootModelBase;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.util.LogtalkDebugLogger;
import com.intellij.plugins.logtalk.util.LogtalkEventLogUtil;
import com.intellij.plugins.logtalk.util.LogtalkFileUtil;
import com.intellij.plugins.haxe.util.LogtalkStringUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Various utilities to work with logtalk libraries.
 */
public class LogtalklibUtil {
  private static LogtalkDebugLogger LOG = LogtalkDebugLogger.getLogger();
  static { LOG.setLevel(Level.DEBUG); } // Remove when finished debugging.

  static Key<VirtualFile> LogtalklibRootKey = new Key<VirtualFile>("Logtalklib.rootDirectory");

  static final String HAXELIB_LOG_ID = "Logtalklib Synchronization";

  // TODO: Figure out what the haxelib configuration is coming from and watch for changes, to invalidate the cache.
  //       (In practice this will not change often.

  /**
   * Get the base path that haxelib uses to store libraries.
   * @param sdk
   * @return
   */
  public static VirtualFile getLibraryBasePath(@NotNull final Sdk sdk) {

    VirtualFile rootDirectory = sdk.getUserData(LogtalklibRootKey);
    if (null == rootDirectory) {
      List<String> output = LogtalklibCommandUtils.issueLogtalklibCommand(sdk, "config");
      for (String s : output) {
        if (s.isEmpty()) continue;
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(s);
        if (null != file) {
          rootDirectory = file;
          sdk.putUserData(LogtalklibRootKey, file);
          break;
        }
      }
    }
    return rootDirectory;
  }


  public static VirtualFile getLibraryRoot(@NotNull Sdk sdk, @NotNull String libName) {
    LocalFileSystem lfs = LocalFileSystem.getInstance();

    VirtualFile haxelibRoot = getLibraryBasePath(sdk);
    String rootName = haxelibRoot.getPath();

    // Forking 'haxelib path' is slow, so we will do what it does without forking.
    // In this case, it locates a subdirectory named $HAXELIB_PATH/libName/.dev, and
    // if it exists, uses the path found in that file.
    // Failing that, it looks for .current in the same path, and uses the semantic
    // version found in that file to compute the path name.
    String libDirName = LogtalkFileUtil.joinPath(rootName, libName);
    VirtualFile libDir = lfs.findFileByPath(libDirName);
    if (null != libDir) {
      // Hidden ".dev" file takes precedence.  It contains the path to the library root.
      VirtualFile dotDev = libDir.findChild(".dev");
      if (null != dotDev) {
        try {
          String libRootName = FileUtil.loadFile(new File(dotDev.getPath()));
          VirtualFile libRoot = lfs.findFileByPath(libRootName);
          if (null != libRoot) {
            return libRoot;
          }
        }
        catch (IOException e) {
          LOG.debug("IOException reading .dev file for library " + libName, e);
        }
      }
      // Hidden ".current" file contains the semantic version (not the path!) of the
      // library that haxelib will use.
      VirtualFile dotCurrent = libDir.findChild(".current");
      if (null != dotCurrent) {
        try {
          String currentVer = FileUtil.loadFile(new File(dotCurrent.getPath()));
          LogtalklibSemVer semver = LogtalklibSemVer.create(currentVer.trim());
          String libRootName = LogtalkFileUtil.joinPath(rootName, libName, semver.toDirString());
          VirtualFile libRoot = lfs.findFileByPath(libRootName);
          if (null != libRoot) {
            return libRoot;
          }
        }
        catch (IOException e) {
          LOG.debug("IOException reading .current file for library " + libName, e);
        }
      }
    } else {
      if (LOG.isDebugEnabled())
        LOG.debug("Couldn't find directory " + libDirName + " for library " + libName);
    }

    // If we got here, then see what haxelib can give us.  This takes >40ms on average.
    List<String> output = LogtalklibCommandUtils.issueLogtalklibCommand(sdk, "path", libName);
    for (String s : output) {
      if (s.isEmpty()) continue;
      if (s.startsWith("-D")) continue;
      if (s.startsWith("-L")) continue;
      if (s.matches("Error: .*")) {
        logWarningEvent(LogtalkBundle.message("haxelib.synchronization.title"), s);
        continue; // break??
      }
      s = FileUtil.normalize(s);
      if (FileUtil.startsWith(s, rootName)) {
        String libClasspath = FileUtil.getRelativePath(rootName, s, '/');
        String[] libParts = libClasspath.split("/");  // Instead of FileUtil.splitpath() because normalized names use '/'
        // First one is always for the current library.
        if (LOG.isDebugEnabled()) {
          if (!libParts[0].equals(libName)) { // TODO: capitalization issue on windows??
            LOG.debug("Library found directory name '" + libParts[0] + "' did not match library name '" +
                      libName + ".");
          }
          // Second is normally the version string.  But that's not *always* the case, if it's a dev path.
          if (!libParts[1].matches(LogtalklibSemVer.VERSION_REGEX)
              && !(new File(LogtalkStringUtil.join("/", rootName, libParts[0], ".dev"))).exists()) {
            LOG.debug("Library version '" + libParts[1] + "' didn't match the regex.");
          }
        }
        String libRoot = FileUtil.join(rootName, libParts[0], libParts[1]);
        VirtualFile srcroot = LocalFileSystem.getInstance().findFileByPath(libRoot);
        if (null == srcroot) {
          logWarningEvent(LogtalkBundle.message("haxelib.synchronization.title"),
                          LogtalkBundle.message("library.source.root.was.not.found.0", libRoot));
        }
        return srcroot;
      }
    }

    logWarningEvent(LogtalkBundle.message("haxelib.synchronization.title"),
                    LogtalkBundle.message("could.not.determine.library.source.root.0", libName));
    return null;
  }


  public static List<String> getInstalledLibraryNames(@NotNull Sdk sdk) {
    final List<String> listCmdOutput = LogtalklibCommandUtils.issueLogtalklibCommand(sdk, "list");
    if ((listCmdOutput.size() > 0) && (! listCmdOutput.get(0).contains("Unknown command"))) {
      final List<String> installedLogtalklibs = new ArrayList<String>();

      for (final String line : listCmdOutput) {
        final String[] tokens = line.split(":");
        installedLogtalklibs.add(tokens[0]);
      }
      return installedLogtalklibs;
    }
    return Collections.emptyList();
  }


  /**
   * Get the libraries for the given module.  This does not include any
   * libraries from projects or SDKs.
   *
   * @param module to look up haxelib for.
   * @return a (possibly empty) collection of libraries.  These are NOT
   *         necessarily properly ordered, but they are unique.
   */
  // XXX: EMB - Not sure that I like this method here.  It could be a static on LogtalkLibraryList,
  //      but that's not so great, either.  The reason I don't like it in this module is
  //      that this has to reach back out to LogtalklibProjectUpdater to find the library cache for
  //      the module.
  @NotNull
  public static LogtalkLibraryList getModuleLibraries(@NotNull final Module module) {
    ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
    if (null == rootManager) return new LogtalkLibraryList(module);

    final LogtalkLibraryList moduleLibs = new LogtalkLibraryList(module);
    if (rootManager instanceof ModuleRootManagerImpl) {
      ModuleRootManagerImpl rootManagerImpl = (ModuleRootManagerImpl)rootManager;

      RootModelBase modelBase = rootManagerImpl.getRootModel(); // Can't fail.
      OrderEnumerator entries = modelBase.orderEntries();       // Can't fail.

      entries.forEachLibrary(new Processor<Library>() {
        @Override
        public boolean process(Library library) {
          LogtalkLibraryReference ref = LogtalkLibraryReference.create(module, library.getName());
          if (null != ref) {
            moduleLibs.add(ref);
          }
          return true;
        }
      });
    } else {
      LOG.assertLog(false, "Expected a ModuleRootManagerImpl, but didn't get one.");
    }

    return moduleLibs;
  }



  /**
   * Get the list of libraries specified on the project.  Managed haxelib
   * (of the form "haxelib|<lib_name>") libraries are included unless
   * filterManagedLibs is true.
   *
   *
   * @param project to get the classpath for.
   * @return a (possibly empty) list of the classpaths for all of the libraries
   *         that are specified on the project (in the library pane).
   */
  @NotNull
  public static LogtalkLibraryList getProjectLibraries(@NotNull Project project, boolean filterManagedLibs, boolean filterUnmanagedLibs) {
    LibraryTable libraryTable = ProjectLibraryTable.getInstance(project);
    if (null == libraryTable || (filterManagedLibs && filterUnmanagedLibs)) {
      return new LogtalkLibraryList(LogtalklibSdkUtils.lookupSdk(project));
    }

    LogtalkLibraryList libs = new LogtalkLibraryList(LogtalklibSdkUtils.lookupSdk(project));
    Library[] libraries = libraryTable.getLibraries();
    for (Library library : libraries) {
      String name = library.getName();
      boolean isManaged = LogtalklibNameUtil.isManagedLibrary(name);
      if (filterManagedLibs && isManaged) continue;
      if (filterUnmanagedLibs && !isManaged) continue;

      libs.add(LogtalkLibraryReference.create(project, name));
    }
    return libs;
  }


  /**
   * Get the relative path from the SDK's library path to the given path.
   * Trim the library path out of the beginning of path.
   * @return The relative path, or null if the path isn't a library path.
   */
  @Nullable
  public static String getLibraryRelativeDirectory(@NotNull Sdk sdk, String path) {
    if (null == path || path.isEmpty()) {
      return null;
    }

    VirtualFile haxelibRoot = getLibraryBasePath(sdk);
    String rootName = haxelibRoot.getPath();

    String s = FileUtil.toSystemIndependentName(path);
    if (FileUtil.startsWith(s, rootName)) {
      return FileUtil.getRelativePath(rootName, s, LogtalkFileUtil.SEPARATOR);
    }
    return null;
  }

  /**
   * Given a path, determine if it is a library path and, if so, what the library name is.
   * @param path file name or URL for a potential logtalk library.
   * @return a library name, if available.
   */
  @Nullable
  public static String deriveLibraryNameFromPath(@NotNull Sdk sdk, String path) {
    String rel = getLibraryRelativeDirectory(sdk, path);
    if (null != rel && !rel.isEmpty()) {
      List<String> libParts = FileUtil.splitPath(rel);
      // First part is the library name.
      return libParts.get(0);
    }
    return null;
  }


  /**
   * Get information derivable from a library classpath.
   * @param sdk
   * @param path
   * @return
   */
  @Nullable
  public static LogtalkLibraryInfo deriveLibraryInfoFromPath(@NotNull Sdk sdk, String path) {
    // TODO: Figure out how to get info from paths that are dev paths?  Don't need that for current callers.

    String rel = getLibraryRelativeDirectory(sdk, path);
    if (null != rel && !rel.isEmpty()) {
      List<String> libParts = LogtalkFileUtil.splitPath(rel);
      if (libParts.size() >= 2) {
        return new LogtalkLibraryInfo(libParts.get(0),   // First part is the name
                                   libParts.get(1),   // Second part is the semantic version
                                   // The rest are the relative classpath.
                                   libParts.size() > 2 ? LogtalkFileUtil.joinPath(libParts.subList(2, libParts.size())) : null);
      }
    }
    return null;
  }


  /**
   * Retrieves the list of dependent logtalk libraries from an XML-based
   * configuration file.
   *
   * @param psiFile name of the configuration file to read
   * @return a list of dependent libraries; may be empty, won't have duplicates.
   */
  @NotNull
  public static LogtalkLibraryList getLogtalklibsFromXmlFile(@NotNull XmlFile psiFile, LogtalklibLibraryCache libraryManager) {
    List<LogtalkLibraryReference> haxelibNewItems = new ArrayList<LogtalkLibraryReference>();

    XmlFile xmlFile = (XmlFile)psiFile;
    XmlDocument document = xmlFile.getDocument();

    if (document != null) {
      XmlTag rootTag = document.getRootTag();
      if (rootTag != null) {
        XmlTag[] haxelibTags = rootTag.findSubTags("haxelib");
        for (XmlTag haxelibTag : haxelibTags) {
          String name = haxelibTag.getAttributeValue("name");
          String ver = haxelibTag.getAttributeValue("version");
          LogtalklibSemVer semver = LogtalklibSemVer.create(ver);
          if (name != null) {
            LogtalkLibrary lib = libraryManager.getLibrary(name, semver);
            if (lib != null) {
              haxelibNewItems.add(lib.createReference(semver));
            } else {
              LOG.warn("Library specified in XML file is not known to haxelib: " + name);
            }
          }
        }
      }
    }

    return new LogtalkLibraryList(libraryManager.getSdk(), haxelibNewItems);
  }


  /**
   * Post an error event to the Event Log.
   *
   * @param title
   * @param details
   */
  public static void logErrorEvent(String title, String... details) {
    LogtalkEventLogUtil.error(HAXELIB_LOG_ID, title, details);
  }

  /**
   * Post a warning event to the Event Log.
   *
   * @param title
   * @param details
   */
  public static void logWarningEvent(String title, String... details) {
    LogtalkEventLogUtil.warn(HAXELIB_LOG_ID, title, details);
  }

  /**
   * Post an informational message to the Event Log.
   *
   * @param title
   * @param details
   */
  public static void logInfoEvent(String title, String... details) {
    LogtalkEventLogUtil.info(HAXELIB_LOG_ID, title, details);
  }

}
