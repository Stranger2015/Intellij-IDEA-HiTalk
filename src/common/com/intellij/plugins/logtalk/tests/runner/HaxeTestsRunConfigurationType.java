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
package com.intellij.plugins.logtalk.tests.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.logtalk.LogtalkBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LogtalkTestsRunConfigurationType implements ConfigurationType {

  private final LogtalkTestsFactory configurationFactory;

  public LogtalkTestsRunConfigurationType() {
    configurationFactory = new LogtalkTestsFactory(this);
  }

  @Override
  public String getDisplayName() {
    return LogtalkBundle.message("logtalk.tests.runner.configuration.name");
  }

  @Override
  public String getConfigurationTypeDescription() {
    return LogtalkBundle.message("logtalk.tests.runner.configuration.name");
  }

  @Override
  public Icon getIcon() {
    return icons.LogtalkIcons.Logtalk_16;
  }

  @NotNull
  @Override
  public String getId() {
    return "LogtalkTestsRunConfiguration";
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{configurationFactory};
  }

  public static class LogtalkTestsFactory extends ConfigurationFactory {

    private LogtalkTestsRunConfigurationType configurationType;

    public LogtalkTestsFactory(ConfigurationType type) {
      super(type);
      configurationType = (LogtalkTestsRunConfigurationType)type;
    }

    public RunConfiguration createTemplateConfiguration(Project project) {
      final String name = LogtalkBundle.message("runner.configuration.name");
      return new LogtalkTestsConfiguration(name, project, configurationType);
    }
  }
}
