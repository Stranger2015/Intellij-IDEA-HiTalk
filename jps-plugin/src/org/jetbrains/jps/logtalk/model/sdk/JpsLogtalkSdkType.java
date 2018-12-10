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
package org.jetbrains.jps.logtalk.model.sdk;

import com.intellij.plugins.logtalk.LogtalkCommonBundle;
import com.intellij.plugins.logtalk.config.sdk.LogtalkSdkAdditionalDataBase;
import com.intellij.plugins.logtalk.config.sdk.impl.LogtalkSdkAdditionalDataBaseImpl;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.logtalk.model.sdk.impl.JpsLogtalkSdkAdditionalDataImpl;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;

/**
 * @author: Fedor.Korotkov
 */
public class JpsLogtalkSdkType extends JpsSdkType<JpsLogtalkSdkAdditionalData> {
  public static final JpsLogtalkSdkType INSTANCE = new JpsLogtalkSdkType();

  public static JpsSdkPropertiesSerializer<JpsLogtalkSdkAdditionalData> createJpsSdkPropertiesSerializer() {
    return new JpsSdkPropertiesSerializer<JpsLogtalkSdkAdditionalData>(LogtalkCommonBundle.message("logtalk.sdk.name"), INSTANCE) {
      @NotNull
      public JpsLogtalkSdkAdditionalData loadProperties(@Nullable final Element propertiesElement) {
        final LogtalkSdkAdditionalDataBase sdkData = XmlSerializer.deserialize(propertiesElement, LogtalkSdkAdditionalDataBaseImpl.class);
        return new JpsLogtalkSdkAdditionalDataImpl(sdkData);
      }

      public void saveProperties(@NotNull final JpsLogtalkSdkAdditionalData properties, @NotNull final Element element) {
        XmlSerializer.serializeInto(properties.getSdkData(), element);
      }
    };
  }
}
