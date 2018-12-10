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
package com.intellij.plugins.logtalk.ide.library;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.roots.libraries.ui.LibraryRootsComponentDescriptor;
import com.intellij.openapi.roots.ui.configuration.FacetsProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugins.haxe.ide.module.LogtalkModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkLibraryType extends LibraryType<DummyLibraryProperties> {
  public static final PersistentLibraryKind<DummyLibraryProperties> HAXE_LIBRARY =
    new PersistentLibraryKind<DummyLibraryProperties>("Logtalk") {
      @NotNull
      @Override
      public DummyLibraryProperties createDefaultProperties() {
        return new DummyLibraryProperties();
      }
    };

  public LogtalkLibraryType() {
    super(HAXE_LIBRARY);
  }

  @NotNull
  @Override
  public String getCreateActionName() {
    return "Logtalk";
  }

  @Override
  public boolean isSuitableModule(@NotNull Module module, @NotNull FacetsProvider facetsProvider) {
    return ModuleType.get(module).equals(LogtalkModuleType.getInstance());
  }

  @Override
  public NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent,
                                                  @Nullable VirtualFile contextDirectory,
                                                  @NotNull Project project) {

    return LibraryTypeService.getInstance()
      .createLibraryFromFiles(createLibraryRootsComponentDescriptor(), parentComponent, contextDirectory, this, project);
  }

  @NotNull
  @Override
  public LibraryRootsComponentDescriptor createLibraryRootsComponentDescriptor() {
    return new LogtalkLibraryRootsComponentDescriptor();
  }

  @Override
  public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<DummyLibraryProperties> component) {
    return null;
  }

  @Override
  public Icon getIcon() {
    return icons.LogtalkIcons.Logtalk_16;
  }

  public static LogtalkLibraryType getInstance() {
    return LibraryType.EP_NAME.findExtension(LogtalkLibraryType.class);
  }
}
