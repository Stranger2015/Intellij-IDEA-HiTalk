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
package com.intellij.plugins.logtalk.model.type;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SpecificTypeReference {
  final protected PsiElement context;

  public SpecificTypeReference(@NotNull PsiElement context) {
    this.context = context;
  }

  static public SpecificTypeReference createArray(@NotNull ResultHolder elementType) {
    return SpecificLogtalkClassReference
      .withGenerics(new LogtalkClassReference("Array", elementType.getElementContext()), new ResultHolder[]{elementType}, null);
  }

  static public SpecificTypeReference createMap(@NotNull ResultHolder keyType, @NotNull ResultHolder valueType) {
    return SpecificLogtalkClassReference
      .withGenerics(new LogtalkClassReference("Map", keyType.getElementContext()), new ResultHolder[]{keyType, valueType}, null);
  }

  public SpecificTypeReference withRangeConstraint(LogtalkRange range) {
    return this;
  }

  static public SpecificLogtalkClassReference getVoid(@NotNull PsiElement context) {
    return primitive("Void", context);
  }

  static public SpecificLogtalkClassReference getBool(@NotNull PsiElement context) {
    return primitive("Bool", context);
  }

  static public SpecificLogtalkClassReference getInt(@NotNull PsiElement context) {
    return primitive("Int", context);
  }

  static public SpecificLogtalkClassReference getInt(@NotNull PsiElement context, int value) {
    return primitive("Int", context, value);
  }

  static public SpecificLogtalkClassReference getDynamic(@NotNull PsiElement context) {
    return primitive("Dynamic", context);
  }

  static public SpecificLogtalkClassReference getUnknown(@NotNull PsiElement context) {
    return primitive("Unknown", context);
  }

  static public SpecificLogtalkClassReference getInvalid(@NotNull PsiElement context) {
    return primitive("@@Invalid", context);
  }

  static public SpecificLogtalkClassReference getIterator(SpecificLogtalkClassReference type) {
    return SpecificLogtalkClassReference.withGenerics(new LogtalkClassReference("Iterator", type.getElementContext()),
                                                   new ResultHolder[]{type.createHolder()});
  }

  static public SpecificLogtalkClassReference primitive(String name, @NotNull PsiElement context) {
    return SpecificLogtalkClassReference.withoutGenerics(new LogtalkClassReference(name, context));
  }

  static public SpecificLogtalkClassReference primitive(String name, @NotNull PsiElement context, Object constant) {
    return SpecificLogtalkClassReference.withoutGenerics(new LogtalkClassReference(name, context), constant);
  }

  final public boolean isUnknown() {
    return this.toStringWithoutConstant().equals("Unknown");
  }

  final public boolean isDynamic() {
    return this.toStringWithoutConstant().equals("Dynamic");
  }

  final public boolean isInvalid() {
    return this.toStringWithoutConstant().equals("@@Invalid");
  }

  final public boolean isVoid() {
    return this.toStringWithoutConstant().equals("Void");
  }

  final public boolean isInt() {
    return this.toStringWithoutConstant().equals("Int");
  }

  final public boolean isNumeric() {
    return isInt() || isFloat();
  }

  final public boolean isBool() {
    return this.toStringWithoutConstant().equals("Bool");
  }

  final public boolean isFloat() {
    return this.toStringWithoutConstant().equals("Float");
  }

  final public boolean isString() {
    return this.toStringWithoutConstant().equals("String");
  }

  final public boolean isArray() {
    if (this instanceof SpecificLogtalkClassReference) {
      final SpecificLogtalkClassReference reference = (SpecificLogtalkClassReference)this;
      return reference.clazz.getName().equals("Array");
    }
    return false;
  }

  final public ResultHolder getArrayElementType() {
    if (isArray()) {
      final ResultHolder[] specifics = ((SpecificLogtalkClassReference)this).specifics;
      if (specifics.length >= 1) return specifics[0];
    }
    return getUnknown(context).createHolder();
  }

  final public ResultHolder getIterableElementType(SpecificTypeReference iterable) {
    if (isArray()) {
      return getArrayElementType();
    }
    // @TODO: Must implement it (it is not int always)
    return getInt(iterable.getElementContext()).createHolder();
  }

  abstract public SpecificTypeReference withConstantValue(Object constantValue);

  //public void mutateConstantValue(Object constantValue) {
//
  //}
  final public SpecificTypeReference withoutConstantValue() {
    return withConstantValue(null);
  }

  public boolean isConstant() {
    return this.getConstant() != null;
  }

  public LogtalkRange getRangeConstraint() {
    return null;
  }

  public Object getConstant() {
    return null;
  }

  final public boolean getConstantAsBool() {
    return LogtalkTypeUtils.getBoolValue(getConstant());
  }

  final public double getConstantAsDouble() {
    return LogtalkTypeUtils.getDoubleValue(getConstant());
  }

  final public int getConstantAsInt() {
    return LogtalkTypeUtils.getIntValue(getConstant());
  }

  @NotNull
  final public PsiElement getElementContext() {
    return context;
  }

  abstract public String toString();

  abstract public String toStringWithoutConstant();

  public String toStringWithConstant() {
    return toString();
  }

  @Nullable
  public ResultHolder access(String name, LogtalkExpressionEvaluatorContext context) {
    return null;
  }

  final public boolean canAssign(SpecificTypeReference type2) {
    return LogtalkTypeCompatible.canAssignToFrom(this, type2);
  }

  final public boolean canAssign(ResultHolder type2) {
    return LogtalkTypeCompatible.canAssignToFrom(this, type2);
  }

  public ResultHolder createHolder() {
    return new ResultHolder(this);
  }
}
