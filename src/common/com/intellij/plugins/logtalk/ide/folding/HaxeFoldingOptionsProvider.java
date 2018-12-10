/*
 * Copyright 2018 Ilya Malanin
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
package com.intellij.plugins.logtalk.ide.folding;

import com.intellij.application.options.editor.CodeFoldingOptionsProvider;
import com.intellij.openapi.options.BeanConfigurable;

public class LogtalkFoldingOptionsProvider extends BeanConfigurable<LogtalkFoldingSettings> implements CodeFoldingOptionsProvider {
  /**
   * This is a stub, which could be used later to add logtalk-specific folding settings
   * <pre><code>
   *  final LogtalkFoldingSettings settings = getInstance();
   *  checkBox(
   *    LogtalkBundle.message("collapse.special.region"),
   *    settings::isCollapseSpecialRegion,
   *    settings::setCollapseSpecialRegion
 *    );
   * </code></pre>
   *
   * @see LogtalkFoldingSettings
   **/
  public LogtalkFoldingOptionsProvider() {
    super(LogtalkFoldingSettings.getInstance());
  }
}
