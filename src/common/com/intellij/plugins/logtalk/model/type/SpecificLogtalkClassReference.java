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

import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.logtalk.model.LogtalkClassModel;
import com.intellij.plugins.logtalk.model.LogtalkGenericParamModel;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.plugins.logtalk.model.type.LogtalkTypeResolver.*;

public class SpecificLogtalkClassReference extends SpecificTypeReference {
  static public SpecificLogtalkClassReference[] EMPTY = new SpecificLogtalkClassReference[0];
  @NotNull final public LogtalkClassReference clazz;

  // @TODO: Change specifics with generics + generic resolver?
  final public ResultHolder[] specifics;
  final public Object constantValue;
  final public LogtalkRange rangeConstraint;

  public SpecificLogtalkClassReference(
    @NotNull LogtalkClassReference clazz,
    ResultHolder[] specifics,
    Object constantValue,
    LogtalkRange rangeConstraint,
    @NotNull PsiElement context
  ) {
    super(context);
    this.clazz = clazz;
    this.specifics = specifics;
    this.constantValue = constantValue;
    this.rangeConstraint = rangeConstraint;
  }

  public LogtalkClassReference getLogtalkClassRef() {
    return this.clazz;
  }

  public LogtalkClass getLogtalkClass() {
    return this.clazz.getLogtalkClass();
  }

  public LogtalkClassModel getLogtalkClassModel() {
    final LogtalkClass aClass = getLogtalkClass();
    ;
    return (aClass != null) ? aClass.getModel() : null;
  }

  public SpecificLogtalkClassReference withConstantValue(Object constantValue) {
    //if (this.constantValue == constantValue) return this;
    return new SpecificLogtalkClassReference(clazz, specifics.clone(), constantValue, null, context);
  }

  //@Override
  //public void mutateConstantValue(Object constantValue) {
  //  this.constantValue = constantValue;
  //}

  @Override
  public SpecificTypeReference withRangeConstraint(LogtalkRange range) {
    if (this.rangeConstraint == range) return this;
    return new SpecificLogtalkClassReference(clazz, specifics.clone(), constantValue, range, context);
  }

  @Override
  public LogtalkRange getRangeConstraint() {
    return this.rangeConstraint;
  }

  @Override
  public Object getConstant() {
    return this.constantValue;
  }

  static public SpecificLogtalkClassReference withoutGenerics(@NotNull LogtalkClassReference clazz) {
    return new SpecificLogtalkClassReference(clazz, ResultHolder.EMPTY, null, null, clazz.elementContext);
  }

  static public SpecificLogtalkClassReference withoutGenerics(@NotNull LogtalkClassReference clazz, Object constantValue) {
    return new SpecificLogtalkClassReference(clazz, ResultHolder.EMPTY, constantValue, null, clazz.elementContext);
  }

  static public SpecificLogtalkClassReference withGenerics(@NotNull LogtalkClassReference clazz, ResultHolder[] specifics) {
    return new SpecificLogtalkClassReference(clazz, specifics, null, null, clazz.elementContext);
  }

  static public SpecificLogtalkClassReference withGenerics(@NotNull LogtalkClassReference clazz, ResultHolder[] specifics, Object constantValue) {
    return new SpecificLogtalkClassReference(clazz, specifics, constantValue, null, clazz.elementContext);
  }

  public String toStringWithoutConstant() {
    String out = this.clazz.getName();
    if (specifics.length > 0) {
      out += "<";
      for (int n = 0; n < specifics.length; n++) {
        if (n > 0) out += ", ";
        out += specifics[n].toString();
      }
      out += ">";
    }
    return out;
  }

  public String toStringWithConstant() {
    String out = toStringWithoutConstant();
    if (constantValue != null) {
      if (out.equals("Int")) {
        out += " = " + (int)LogtalkTypeUtils.getDoubleValue(constantValue);
      }
      else if (out.equals("String")) {
        out += " = " + constantValue + "";
      }
      else {
        out += " = " + constantValue;
      }
    }
    if (rangeConstraint != null) {
      out += " [" + rangeConstraint + "]";
    }
    return out;
  }

  @Override
  public String toString() {
    //return toStringWithoutConstant();
    return toStringWithConstant();
  }

  public LogtalkGenericResolver getGenericResolver() {
    LogtalkGenericResolver resolver = new LogtalkGenericResolver();
    LogtalkClassModel model = getLogtalkClassModel();
    if (model != null) {
      List<LogtalkGenericParamModel> params = model.getGenericParams();
      for (int n = 0; n < params.size(); n++) {
        LogtalkGenericParamModel paramModel = params.get(n);
        ResultHolder specific = (n < specifics.length) ? this.specifics[n] : getUnknown(context).createHolder();
        resolver.resolvers.put(paramModel.getName(), specific);
      }
    }
    return resolver;
  }

  @Nullable
  @Override
  public ResultHolder access(String name, LogtalkExpressionEvaluatorContext context) {
    if (this.isDynamic()) return this.withoutConstantValue().createHolder();

    if (name == null) {
      return null;
    }
    LogtalkClass aClass = this.clazz.getLogtalkClass();
    if (aClass == null) {
      return null;
    }
    AbstractLogtalkNamedComponent field = (AbstractLogtalkNamedComponent)aClass.findLogtalkFieldByName(name);
    AbstractLogtalkNamedComponent method = (AbstractLogtalkNamedComponent)aClass.findLogtalkMethodByName(name);
    if (method != null) {
      if (context.root == method) return null;
      return getMethodFunctionType(method, getGenericResolver());
    }
    if (field != null) {
      if (context.root == field) return null;
      return getFieldOrMethodReturnType(field, getGenericResolver());
    }
    return null;
  }
}
