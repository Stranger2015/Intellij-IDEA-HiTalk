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
package com.intellij.plugins.logtalk.ide.index;

import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkClassInfoExternalizer implements DataExternalizer<LogtalkClassInfo> {

  private final ThreadLocal<byte[]> buffer = ThreadLocal.withInitial(() -> IOUtil.allocReadWriteUTFBuffer());

  @Override
  public void save(@NotNull DataOutput out, LogtalkClassInfo classInfo) throws IOException {
    IOUtil.writeUTFFast(buffer.get(), out, classInfo.getValue());
    final LogtalkComponentType haxeComponentType = classInfo.getType();
    final int key = haxeComponentType == null ? -1 : haxeComponentType.getKey();
    out.writeInt(key);
  }

  @Override
  public LogtalkClassInfo read(@NotNull DataInput in) throws IOException {
    final String value = IOUtil.readUTFFast(buffer.get(), in);
    final int key = in.readInt();
    return new LogtalkClassInfo(value, LogtalkComponentType.valueOf(key));
  }
}
