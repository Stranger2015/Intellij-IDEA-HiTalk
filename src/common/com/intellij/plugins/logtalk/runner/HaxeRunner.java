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
package com.intellij.plugins.logtalk.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.compilation.LogtalkCompilerUtil;
import com.intellij.plugins.logtalk.config.LogtalkTarget;
import com.intellij.plugins.haxe.ide.module.LogtalkModuleSettings;
import com.intellij.plugins.logtalk.util.LogtalkCommandLine;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkRunner extends DefaultProgramRunner {
  public static final String HAXE_RUNNER_ID = "LogtalkRunner";

  @NotNull
  @Override
  public String getRunnerId() {
    return HAXE_RUNNER_ID;
  }

  @Override
  protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
    final LogtalkApplicationConfiguration configuration = (LogtalkApplicationConfiguration)env.getRunProfile();
    final Module module = configuration.getConfigurationModule().getModule();

    if (module == null) {
      throw new ExecutionException(LogtalkBundle.message("no.module.for.run.configuration", configuration.getName()));
    }

    final LogtalkModuleSettings settings = LogtalkModuleSettings.getInstance(module);

    if (settings.isUseNmmlToBuild()) {
      final NMERunningState nmeRunningState = new NMERunningState(env, module, false);
      return super.doExecute(nmeRunningState, env);
    }

    if (settings.isUseOpenFLToBuild()) {
      final OpenFLRunningState openflRunningState = new OpenFLRunningState(env, module, false);
      return super.doExecute(openflRunningState, env);
    }

    if (configuration.isCustomFileToLaunch() && FileUtilRt.extensionEquals(configuration.getCustomFileToLaunchPath(), "n")) {
      final NekoRunningState nekoRunningState = new NekoRunningState(env, module, configuration.getCustomFileToLaunchPath());
      return super.doExecute(nekoRunningState, env);
    }

    if (configuration.isCustomExecutable()) {
      final String filePath = configuration.isCustomFileToLaunch()
                              ? configuration.getCustomFileToLaunchPath()
                              : getOutputFilePath(module, settings);
      return super.doExecute(new CommandLineState(env) {
        @NotNull
        @Override
        protected ProcessHandler startProcess() throws ExecutionException {
          final LogtalkCommandLine commandLine = new LogtalkCommandLine(module);
          commandLine.withWorkDirectory(PathUtil.getParentPath(module.getModuleFilePath()));
          commandLine.setExePath(configuration.getCustomExecutablePath());
          commandLine.addParameter(filePath);

          final TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(module.getProject());
          setConsoleBuilder(consoleBuilder);

          return new ColoredProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
        }
      }, env);
    }

    if (configuration.isCustomFileToLaunch()) {
      BrowserUtil.open(configuration.getCustomFileToLaunchPath());
      return null;
    }

    if (settings.getLogtalkTarget() == LogtalkTarget.FLASH) {
      BrowserUtil.open(getOutputFilePath(module, settings));
      return null;
    }

    if (settings.getLogtalkTarget() != LogtalkTarget.NEKO) {
      throw new ExecutionException(LogtalkBundle.message("logtalk.run.wrong.target", settings.getLogtalkTarget()));
    }

    final NekoRunningState nekoRunningState = new NekoRunningState(env, module, null);
    return super.doExecute(nekoRunningState, env);
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof LogtalkApplicationConfiguration;
  }

  static String getOutputFilePath(Module module, LogtalkModuleSettings settings) {
    FileDocumentManager.getInstance().saveAllDocuments();
    return LogtalkCompilerUtil.calculateCompilerOutput(module);
  }
}
