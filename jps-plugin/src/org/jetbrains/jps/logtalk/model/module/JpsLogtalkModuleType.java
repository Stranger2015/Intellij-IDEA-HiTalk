/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2014 AS3Boyan
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
package org.jetbrains.jps.logtalk.model.module;

import com.intellij.plugins.logtalk.module.impl.LogtalkModuleSettingsBaseImpl;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.logtalk.model.module.impl.JpsLogtalkModuleSettingsImpl;
import org.jetbrains.jps.model.ex.JpsElementTypeBase;
import org.jetbrains.jps.model.module.JpsModuleType;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

/**
 * @author: Fedor.Korotkov
 */
public class JpsLogtalkModuleType extends JpsElementTypeBase<JpsLogtalkModuleSettings> implements JpsModuleType<JpsLogtalkModuleSettings> {
  public static final JpsLogtalkModuleType INSTANCE = new JpsLogtalkModuleType();
  private static final String ID = "HAXE_MODULE";

  private JpsLogtalkModuleType() {
  }

  public static JpsModulePropertiesSerializer<JpsLogtalkModuleSettings> createModulePropertiesSerializer() {
    return new JpsModulePropertiesSerializer<JpsLogtalkModuleSettings>(INSTANCE, ID, "LogtalkModuleSettingsStorage") {
      @Override
      public JpsLogtalkModuleSettings loadProperties(@Nullable final Element componentElement) {
        final LogtalkModuleSettingsBaseImpl moduleSettingsBase;
        if (componentElement != null) {
          moduleSettingsBase = XmlSerializer.deserialize(componentElement, LogtalkModuleSettingsBaseImpl.class);
        }
        else {
          moduleSettingsBase = new LogtalkModuleSettingsBaseImpl();
        }
        return new JpsLogtalkModuleSettingsImpl(moduleSettingsBase);
      }

      @Override
      public void saveProperties(@NotNull final JpsLogtalkModuleSettings settings, @NotNull final Element componentElement) {
        XmlSerializer.serializeInto(settings.getProperties(), componentElement);
      }
    };
  }
}
