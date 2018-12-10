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
package com.intellij.plugins.logtalk.model;

import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiCompositeElement;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogtalkEnumModelImpl extends LogtalkClassModel implements LogtalkEnumModel {
  public LogtalkEnumModelImpl(@NotNull LogtalkEnumDeclaration haxeClass) {
    super(haxeClass);
  }

  public LogtalkEnumDeclaration getEnumDeclaration() {
    return (LogtalkEnumDeclaration)haxeClass;
  }

  @Override
  public LogtalkClassModel getParentClass() {
    return null;
  }

  @Override
  public List<LogtalkClassReferenceModel> getInterfaceExtendingInterfaces() {
    return Collections.emptyList();
  }

  @Override
  public List<LogtalkClassReferenceModel> getImplementingInterfaces() {
    return Collections.emptyList();
  }

  @Override
  public boolean isClass() {
    return false;
  }

  @Override
  public boolean isInterface() {
    return false;
  }

  @Override
  public boolean isEnum() {
    return true;
  }

  @Override
  public boolean isTypedef() {
    return false;
  }

  @Nullable
  @Override
  public LogtalkTypeOrAnonymous getUnderlyingType() {
    return null;
  }

  @Override
  public List<LogtalkType> getAbstractToList() {
    return Collections.emptyList();
  }

  @Override
  public List<LogtalkType> getAbstractFromList() {
    return Collections.emptyList();
  }

  @Override
  public boolean hasMethod(String name) {
    return false;
  }

  @Override
  public boolean hasMethodSelf(String name) {
    return false;
  }

  @Override
  public LogtalkMethodModel getMethodSelf(String name) {
    return null;
  }

  @Override
  public LogtalkMethodModel getConstructorSelf() {
    return null;
  }

  @Override
  public LogtalkMethodModel getConstructor() {
    return null;
  }

  @Override
  public boolean hasConstructor() {
    return false;
  }

  @Override
  public LogtalkMethodModel getParentConstructor() {
    return null;
  }

  @Override
  public LogtalkMemberModel getMember(@NotNull final String name) {
    return getValue(name);
  }

  @Override
  public List<LogtalkMemberModel> getMembers() {
    return getValuesStream().collect(Collectors.toList());
  }

  @NotNull
  @Override
  public List<LogtalkMemberModel> getMembersSelf() {
    return getMembers();
  }


  @Override
  public LogtalkEnumValueModel getValue(@NotNull String name) {
    LogtalkEnumValueDeclaration value = getValueDeclarationsStream()
      .filter(declaration -> name.equals(declaration.getName()))
      .findFirst()
      .orElse(null);

    return value != null ? (LogtalkEnumValueModel)value.getModel() : null;
  }

  @Override
  public List<LogtalkEnumValueModel> getValues() {
    return getValuesStream().collect(Collectors.toList());
  }

  public Stream<LogtalkEnumValueModel> getValuesStream() {
    return getValueDeclarationsStream()
      .map(declaration -> (LogtalkEnumValueModel)declaration.getModel());
  }

  private Stream<LogtalkEnumValueDeclaration> getValueDeclarationsStream() {
    LogtalkEnumBody body = getEnumBodyPsi();

    return body != null ? body.getEnumValueDeclarationList().stream() : Stream.empty();
  }

  @Nullable
  private LogtalkEnumBody getEnumBodyPsi() {
    return getEnumDeclaration().getEnumBody();
  }

  @Override
  public LogtalkFieldModel getField(String name) {
    return null;
  }

  @Override
  public List<LogtalkFieldModel> getFields() {
    return Collections.emptyList();
  }

  @Override
  public LogtalkMethodModel getMethod(String name) {
    return null;
  }

  @Override
  public List<LogtalkMethodModel> getMethods() {
    return Collections.emptyList();
  }

  @Override
  public List<LogtalkMethodModel> getMethodsSelf() {
    return Collections.emptyList();
  }

  @Override
  public List<LogtalkMethodModel> getAncestorMethods() {
    return Collections.emptyList();
  }

  @NotNull
  @Override
  public LogtalkClass getPsi() {
    return super.getPsi();
  }

  @Nullable
  @Override
  public LogtalkPsiCompositeElement getBodyPsi() {
    return getEnumBodyPsi();
  }

  @Override
  public List<LogtalkGenericParamModel> getGenericParams() {
    return super.getGenericParams();
  }

  @Override
  public List<LogtalkModel> getExposedMembers() {
    return new SmartList<>(getValues());
  }

  @Override
  public boolean isPublic() {
    return super.isPublic();
  }
}
