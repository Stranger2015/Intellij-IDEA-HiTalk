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

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LogtalkRunConfigurationType implements ConfigurationType {
  private final LogtalkFactory configurationFactory;

  public LogtalkRunConfigurationType() {
    configurationFactory = new LogtalkFactory(this);
  }

  public static LogtalkRunConfigurationType getInstance() {
    return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), LogtalkRunConfigurationType.class);
  }

  public String getDisplayName() {
    return LogtalkBundle.message("runner.configuration.name");
  }

  public String getConfigurationTypeDescription() {
    return LogtalkBundle.message("runner.configuration.name");
  }

  public Icon getIcon() {
    return icons.LogtalkIcons.Logtalk_16;
  }

  @NotNull
  public String getId() {
    return "LogtalkApplicationRunConfiguration";
  }

  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{configurationFactory};
  }

  public static class LogtalkFactory extends ConfigurationFactory {

    public LogtalkFactory(ConfigurationType type) {
      super(type);
    }

    public RunConfiguration createTemplateConfiguration(Project project) {
      final String name = LogtalkBundle.message("runner.configuration.name");
      return new LogtalkApplicationConfiguration(name, project, getInstance());
    }
  }
}
