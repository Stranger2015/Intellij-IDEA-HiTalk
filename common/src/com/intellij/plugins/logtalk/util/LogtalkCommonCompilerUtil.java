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
package com.intellij.plugins.logtalk.util;

import com.intellij.execution.process.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugins.logtalk.LogtalkCommonBundle;
import com.intellij.plugins.logtalk.compilation.LogtalkCompilerProcessHandler;
import com.intellij.plugins.logtalk.config.LogtalkTarget;
import com.intellij.plugins.logtalk.config.NMETarget;
import com.intellij.plugins.logtalk.config.OpenFLTarget;
import com.intellij.plugins.logtalk.config.sdk.LogtalkSdkAdditionalDataBase;
import com.intellij.plugins.logtalk.module.LogtalkModuleSettingsBase;
import com.intellij.util.BooleanValueHolder;
import com.intellij.util.PathUtil;
import com.intellij.util.text.StringTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkCommonCompilerUtil {
  public interface CompilationContext {

    LogtalkSdkAdditionalDataBase getLogtalkSdkData();

    @NotNull
    LogtalkModuleSettingsBase getModuleSettings();

    String getModuleName();

    String getCompilationClass();
    String getOutputFileName();
    String getOutputDirectory();
    Boolean getIsTestBuild();

    void errorHandler(String message);
    void warningHandler(String message);
    void infoHandler(String message);

    void log(String message);

    String getSdkHomePath();

    public String getLogtalklibPath();

    public String getNekoBinPath();

    boolean isDebug();

    String getSdkName();

    List<String> getSourceRoots();

    String getModuleDefaultCompileOutputPath();

    void setErrorRoot(String root);

    String getErrorRoot();

    void handleOutput(String[] lines);

    LogtalkTarget getLogtalkTarget();

    String getModuleDirPath();
  }

  private static final Logger LOG = Logger.getInstance("#LogtalkCommonCompilerUtil");

  public static boolean compile(final CompilationContext context) {
    LogtalkModuleSettingsBase settings = context.getModuleSettings();
    if (settings.isExcludeFromCompilation()) {
      context.log(LogtalkCommonBundle.message("module.0.is.excluded.from.compilation", context.getModuleName()));
      return true;
    }

    if (!verifyProjectSettings(context)) {
      return false;
    }

    final String workingPath = calculateWorkingPath(context);
    if (! workingPath.isEmpty()) {
      context.setErrorRoot(workingPath);
    }
    final File workingDirectory = new File(FileUtil.toSystemDependentName(workingPath));
    if (!workingDirectory.exists()) {
      if (!workingDirectory.mkdirs()) {
        context.errorHandler(LogtalkCommonBundle.message("output.path.not.found", workingPath));
        return false;
      }
    }

    final List<List<String>> commandLines = generateCommandLines(context);

    final BooleanValueHolder hasErrors = new BooleanValueHolder(false);
    try {
      for (List<String> commandLine : commandLines) {

        // Show the command line in the output window.
        // TODO: Make a checkbox in the SDK configuration window to enable/disable showing the command line.
        String commandLineString = LogtalkCommonBundle.message("compiler.command.line", String.join(" ", commandLine));

        // Output extra debug information to the console window. Note that process output, and these lines,
        // in particular, are kept in a LinkedHashSet (internally, a HashMap).  Duplicate lines (having the
        // same hash value) are NOT added to the set, so these will not be repeated in the output when multiple
        // commands are run.  For this reason, the lime banner is also not repeated in the output when it runs
        // a second time.
        context.infoHandler(LogtalkCommonBundle.message("compiler.working.path", workingPath));
        context.infoHandler(LogtalkCommonBundle.message("compiler.output.path", context.getModuleDefaultCompileOutputPath()));
        context.infoHandler(LogtalkCommonBundle.message("compiler.output.file", context.getOutputFileName()));

        ProcessBuilder process = LogtalkSdkUtilBase.createProcessBuilder(commandLine, workingDirectory, context.getLogtalkSdkData());
        final BaseOSProcessHandler handler = new LogtalkCompilerProcessHandler(
          context,
          process.start(),
          commandLineString,
          Charset.defaultCharset()
        );

        handler.addProcessListener(new ProcessAdapter() {
          @Override
          public void processTerminated(ProcessEvent event) {
            int exitcode = event.getExitCode();
            hasErrors.setValue(exitcode != 0);
            if (exitcode < 0) {
              context.infoHandler(LogtalkCommonBundle.message("negative.error.code.message"));
            }

            super.processTerminated(event);
          }
        });

        handler.startNotify();
        handler.waitFor();
      }
    }
    catch (IOException e) {
      context.errorHandler(LogtalkCommonBundle.message("process.threw.exception", e.getMessage()));
      hasErrors.setValue(true);
      return false;
    }

    return !hasErrors.getValue();
  }

  private static boolean verifyProjectSettings(CompilationContext context) {
    final LogtalkModuleSettingsBase settings = context.getModuleSettings();
    final String mainClass = context.getCompilationClass();
    final String fileName = context.getOutputFileName();
    boolean requiresLogtalklib = false;

    boolean err = false;

    if (settings.isUseUserPropertiesToBuild()) {

      err |= !verifyNonEmptyString(mainClass, context, "no.main.class.for.module");
      err |= !verifyNonEmptyString(fileName, context, "no.output.file.name.for.module");
      err |= !verifyTargetIsSet(context.getLogtalkTarget(), context);

    } else if (settings.isUseHxmlToBuild()) {

      err |= !verifyTargetIsSet(context.getLogtalkTarget(), context);
      err |= !verifyNonEmptyString(settings.getHxmlPath(), context, "no.project.file.for.module");

    } else if (settings.isUseNmmlToBuild()) {

      requiresLogtalklib = true;
      err |= !verifyTargetIsSet(settings.getNmeTarget(), context);
      err |= !verifyNonEmptyString(settings.getNmmlPath(), context, "no.project.file.for.module");

    } else if (settings.isUseOpenFLToBuild()) {

      requiresLogtalklib = true;
      err |= !verifyTargetIsSet(settings.getOpenFLTarget(), context);
      err |= !verifyNonEmptyString(settings.getOpenFLPath(), context, "no.project.file.for.module");

    } else {
      err |= true;
      context.errorHandler(LogtalkCommonBundle.message("error.unknown.project.settings.type.for.module.0", context.getModuleName()));
    }

    err |= !verifyNonEmptyString(context.getSdkHomePath(), context, "no.sdk.for.module");
    err |= !verifyNonEmptyString(LogtalkSdkUtilBase.getCompilerPathByFolderPath(context.getSdkHomePath()),
                                 context, "invalid.logtalk.sdk.for.module");

    if ( requiresLogtalklib ) {
      err |= !verifyNonEmptyString(context.getNekoBinPath(), context, "no.nekopath.for.sdk");
      err |= !verifyNonEmptyString(context.getLogtalklibPath(), context, "no.haxelib.for.sdk");
    }

    return !err;
  }

  private static boolean verifyTargetIsSet(Object target, CompilationContext context) {
    if (null == target) {
      context.errorHandler(LogtalkCommonBundle.message("no.target.for.module", context.getModuleName()));
      return false;
    }
    return true;
  }

  private static boolean verifyNonEmptyString(String s, CompilationContext context,
                                              @PropertyKey(resourceBundle = LogtalkCommonBundle.BUNDLE) String propertyKey) {
    if (s == null || s.isEmpty()) {
      context.errorHandler(LogtalkCommonBundle.message(propertyKey, context.getModuleName()));
      return false;
    }
    return true;
  }


  @NotNull
  private static String calculateWorkingPath(CompilationContext context) {
    LogtalkModuleSettingsBase settings = context.getModuleSettings();

    // TODO: Add a setting for the working directory to the project/module settings dialog.  Then use that here.

    String workingPath = null;

    if (settings.isUseOpenFLToBuild()) {
      // Use the module directory...
      workingPath = context.getModuleDirPath();
    } else if (settings.isUseNmmlToBuild()) {
      String nmmlPath = settings.getNmmlPath();
      workingPath = PathUtil.getParentPath(nmmlPath);
    } else if (settings.isUseHxmlToBuild()) {
      String hxmlPath = settings.getHxmlPath();
      workingPath = PathUtil.getParentPath(hxmlPath);
    } else if (settings.isUseUserPropertiesToBuild()) {
      workingPath = findCwdInCommandLineArguments(settings);
    }

    if (null  == workingPath || workingPath.isEmpty()) {
       workingPath = context.getModuleDirPath();  // Last ditch effort. Location of the .iml
    }
    return null == workingPath ? "" : workingPath;
  }

  /**
   * Figure out where the compiler's output will go.
   *
   * Caveat: This doesn't attempt to read Logtalk's project files to determine the actual
   * output if a project file is used.  Rather it tries to determine what the user wants
   * by using IDEA's module and project settings.  Use accordingly.
   *
   * @param context
   * @return File name if the target is a file; directory name if the target has multiple files.
   */
  public static String calculateOutputPath(CompilationContext context) {
    if (!context.getModuleSettings().isUseUserPropertiesToBuild()) {
      LOG.error("Programming Error: Unexpected output target for this routine.");
    }

    // Directory priorities:
    // - Absolute directory in the output file name (from settings)
    // - Specified output directory (from settings)
    // - '--cwd' argument (from compiler arguments in settings)
    // Should include, but we don't have an extension:- Module extension's default path (which may inherit from the project).
    // - Project's configured output path.
    // - Module directory.

    String outputFile = LogtalkFileUtil.normalize(context.getOutputFileName());
    if (LogtalkFileUtil.isAbsolutePath(outputFile)) {
      return outputFile;
    }

    String outputRoot = LogtalkFileUtil.normalize(context.getOutputDirectory());
    if (outputRoot.isEmpty()) {
      // User didn't tell us, so guess the best one.
      outputRoot = LogtalkFileUtil.normalize(findCwdInCommandLineArguments(context.getModuleSettings()));
      if (outputRoot.isEmpty()) {
        outputRoot = LogtalkFileUtil.normalize(context.getModuleDefaultCompileOutputPath());
        if (outputRoot.isEmpty()) {
          outputRoot = LogtalkFileUtil.normalize(context.getModuleDirPath());
        }
      }
    }

    // Be a little smart and don't add directories if the user has already specified them.
    if (outputFile.contains(LogtalkFileUtil.SEPARATOR_STRING)) {
      return LogtalkFileUtil.joinPath(outputRoot, outputFile);
    }

    LogtalkTarget target = context.getLogtalkTarget();
    return LogtalkFileUtil.joinPath(outputRoot,
                 "Export",
                 target != null ? target.getDefaultOutputSubdirectory() : "",
                 context.isDebug() ? "debug" : "release",
                 outputFile);
  }

  private static final Pattern CWD_PATTERN = Pattern.compile("--cwd[ \t]+('[^']*'|\"[^\"]*\"|(\\ |[^ \t])+)");
  private static String findCwdInCommandLineArguments(LogtalkModuleSettingsBase settings) {
    String cl = settings.getArguments();

    Matcher m = CWD_PATTERN.matcher(cl);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }


  private static List<List<String>> generateCommandLines(CompilationContext context) {
    List<List<String>> clList = new ArrayList<List<String>>();
    LogtalkModuleSettingsBase settings = context.getModuleSettings();

    if (settings.isUseOpenFLToBuild()) {
      clList.addAll(generateOpenflCommands(context));
    }
    else if (settings.isUseNmmlToBuild()) {
      clList.add(generateNmeCommand(context));
    }
    else if (settings.isUseHxmlToBuild()) {
      clList.add(generateHxmlCommand(context));
    }
    else {
      clList.add(generateUserPropertiesCommand(context));
    }

    return clList;
  }


  private static List<String> generateHxmlCommand(CompilationContext context) {

    final List<String> commandLine = new ArrayList<String>();
    final String sdkExePath = LogtalkSdkUtilBase.getCompilerPathByFolderPath(context.getSdkHomePath());
    commandLine.add(sdkExePath);

    String hxmlPath = context.getModuleSettings().getHxmlPath();
    commandLine.add(FileUtil.toSystemDependentName(hxmlPath));
    if (context.isDebug() && context.getLogtalkTarget() == LogtalkTarget.FLASH) {
      commandLine.add("-D");
      commandLine.add("fdb");
      commandLine.add("-debug");
    }
    return commandLine;
  }

  private static List<String> generateUserPropertiesCommand(CompilationContext context) {

    final List<String> commandLine = new ArrayList<String>();
    final String sdkExePath = LogtalkSdkUtilBase.getCompilerPathByFolderPath(context.getSdkHomePath());
    commandLine.add(sdkExePath);


    final LogtalkModuleSettingsBase settings = context.getModuleSettings();
    commandLine.add("-main");
    commandLine.add(context.getCompilationClass());

    final StringTokenizer argumentsTokenizer = new StringTokenizer(settings.getArguments());
    while (argumentsTokenizer.hasMoreTokens()) {
      commandLine.add(argumentsTokenizer.nextToken());
    }

    if (context.isDebug()) {
      commandLine.add("-debug");
    }
    if (context.getLogtalkTarget() == LogtalkTarget.FLASH && context.isDebug()) {
      commandLine.add("-D");
      commandLine.add("fdb");
    }

    for (String sourceRoot : context.getSourceRoots()) {
      commandLine.add("-cp");
      commandLine.add(sourceRoot);
    }

    commandLine.add(context.getLogtalkTarget().getCompilerFlag());
    commandLine.add(calculateOutputPath(context));
    return commandLine;
  }

  private static List<String> generateNmeCommand(CompilationContext context) {

    final List<String> commandLine = new ArrayList<>();
    final String haxelibPath = context.getLogtalklibPath();
    commandLine.add(haxelibPath);

    final LogtalkModuleSettingsBase settings = context.getModuleSettings();
    commandLine.add("run");
    commandLine.add("nme");
    commandLine.add("build");
    commandLine.add(settings.getNmmlPath());
    commandLine.add(settings.getNmeTarget().getTargetFlag());
    if (context.isDebug()) {
      commandLine.add("-debug");
      commandLine.add("-Ddebug");
    }
    if (settings.getNmeTarget() == NMETarget.FLASH && context.isDebug()) {
      commandLine.add("-Dfdb");
    }
    final StringTokenizer flagsTokenizer = new StringTokenizer(settings.getNmeFlags());
    while (flagsTokenizer.hasMoreTokens()) {
      commandLine.add(flagsTokenizer.nextToken());
    }
    return commandLine;
  }

  private static List<List<String>> generateOpenflCommands(CompilationContext context) {

    final String haxelibPath = context.getLogtalklibPath();
    final LogtalkModuleSettingsBase settings = context.getModuleSettings();

    List<List<String>> clList = new ArrayList<>();

    String cmds[] = {"update", "build"};
    for (String cmd : cmds) {

      List<String> commandLine = new ArrayList<>();
      commandLine.add(haxelibPath);

      commandLine.add("run");
      commandLine.add("lime");
      commandLine.add(cmd);

      // XXX: Isn't this an error if the openfl project file is missing?
      if(!StringUtil.isEmpty(settings.getOpenFLPath())) {
        commandLine.add(settings.getOpenFLPath());
      }

      commandLine.add(settings.getOpenFLTarget().getTargetFlag());

      commandLine.add("-verbose");

      if (context.isDebug()) {
        commandLine.add("-debug");
        commandLine.add("-Ddebug");

        if (settings.getOpenFLTarget() == OpenFLTarget.FLASH) {
          commandLine.add("-Dfdb");
        }
      }

      final StringTokenizer flagsTokenizer = new StringTokenizer(settings.getOpenFLFlags());
      while (flagsTokenizer.hasMoreTokens()) {
        commandLine.add(flagsTokenizer.nextToken());
      }

      clList.add(commandLine);
    }
    return clList;
  }
}
