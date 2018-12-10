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
package com.intellij.plugins.logtalk.ide;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.plugins.haxe.lang.psi.LogtalkComponent;
import com.intellij.plugins.haxe.lang.psi.LogtalkFile;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkIconProvider extends IconProvider {
  @Override
  public Icon getIcon(@NotNull PsiElement element, @Iconable.IconFlags int flags) {
    if (element instanceof LogtalkFile) {
      return getLogtalkFileIcon((LogtalkFile)element, flags);
    }
    return null;
  }

  @Nullable
  private static Icon getLogtalkFileIcon(LogtalkFile file, @Iconable.IconFlags int flags) {
    final String fileName = FileUtil.getNameWithoutExtension(file.getName());
    for (LogtalkComponent component : LogtalkResolveUtil.findComponentDeclarations(file)) {
      if (fileName.equals(component.getName())) {
        return component.getIcon(flags);
      }
    }
    return null;
  }
}
