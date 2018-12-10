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
package com.intellij.plugins.logtalk.model;

import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.haxe.model.type.*;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkMethodPsiMixin;
import com.intellij.plugins.logtalk.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LogtalkMethodModel extends LogtalkMemberModel implements LogtalkExposableModel {
  private LogtalkMethodPsiMixin haxeMethod;

  public LogtalkMethodModel(LogtalkMethodPsiMixin haxeMethod) {
    super(haxeMethod);
    this.haxeMethod = haxeMethod;
  }

  @Override
  public PsiElement getBasePsi() {
    return haxeMethod;
  }

  public LogtalkMethodPsiMixin getMethodPsi() {
    return haxeMethod;
  }

  public PsiElement getBodyPsi() {
    PsiElement[] children = haxeMethod.getChildren();
    if (children.length == 0) return null;
    return children[children.length - 1];
  }

  public List<LogtalkParameterModel> getParameters() {
    List<LogtalkParameterModel> _parameters = new ArrayList<LogtalkParameterModel>();
    LogtalkParameterList parameterList = UsefulPsiTreeUtil.getChild(this.haxeMethod, LogtalkParameterList.class);
    if (parameterList != null) {
      for (LogtalkParameter parameter : parameterList.getParameterList()) {
        _parameters.add(new LogtalkParameterModel(parameter));
      }
    }
    return _parameters;
  }

  public int getParameterCount() {
    LogtalkParameterList parameterList = UsefulPsiTreeUtil.getChild(this.haxeMethod, LogtalkParameterList.class);
    return null == parameterList ? 0 : parameterList.getParametersCount();
  }

  public List<LogtalkParameterModel> getParametersWithContext(LogtalkMethodContext context) {
    List<LogtalkParameterModel> params = getParameters();
    if (context.isExtensionMethod()) {
      params = new ArrayList<LogtalkParameterModel>(params);
      params.remove(0);
    }
    return params;
  }

  @Nullable
  public LogtalkTypeTag getReturnTypeTagPsi() {
    return UsefulPsiTreeUtil.getChild(this.haxeMethod, LogtalkTypeTag.class);
  }

  public PsiElement getReturnTypeTagOrNameOrBasePsi() {
    LogtalkTypeTag psi = getReturnTypeTagPsi();
    return (psi != null) ? psi : getNameOrBasePsi();
  }

  private LogtalkClassModel _declaringClass = null;

  public LogtalkClassModel getDeclaringClass() {
    if (_declaringClass == null) {
      LogtalkClass aClass = (LogtalkClass)this.haxeMethod.getContainingClass();
      _declaringClass = (aClass != null) ? aClass.getModel() : null;
    }
    return _declaringClass;
  }

  public String getFullName() {
    return this.getDeclaringClass().getName() + "." + this.getName();
  }

  public boolean isConstructor() {
    return this.getName().equals(LogtalkTokenTypes.ONEW.toString());
  }

  public boolean isStaticInit() {
    return this.getName().equals("__init__");
  }

  public boolean isArrayAccessor() {
    // Would be nice if this worked, but it won't until the lexer and/or parser stops using MACRO_ID:
    //   return null != UsefulPsiTreeUtil.getChild(this.haxeMethod, LogtalkArrayAccessMeta.class);
    for (LogtalkCustomMeta meta : UsefulPsiTreeUtil.getChildren(this.getMethodPsi(), LogtalkCustomMeta.class)) {
      if ("@:arrayAccess".equals(meta.getText())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getPresentableText(LogtalkMethodContext context) {
    String out = "";
    out += this.getName();
    out += "(";
    int index = 0;
    for (LogtalkParameterModel param : this.getParametersWithContext(context)) {
      if (index > 0) out += ", ";
      out += param.getPresentableText();
      index++;
    }
    out += ")";
    if (!isConstructor()) {
      out += ":" + getResultType();
    }
    return out;
  }

  public SpecificFunctionReference getFunctionType() {
    return getFunctionType(null);
  }

  public ResultHolder getReturnType(@Nullable LogtalkGenericResolver resolver) {
    return LogtalkTypeResolver.getFieldOrMethodReturnType((AbstractLogtalkNamedComponent)this.getBasePsi(), resolver);
  }

  public SpecificFunctionReference getFunctionType(@Nullable LogtalkGenericResolver resolver) {
    LinkedList<ResultHolder> args = new LinkedList<ResultHolder>();
    for (LogtalkParameterModel param : this.getParameters()) {
      args.add(param.getType(resolver));
    }
    return new SpecificFunctionReference(args, getReturnType(resolver), this, haxeMethod);
  }

  public LogtalkMethodModel getParentMethod() {
    final LogtalkClassModel aClass = getDeclaringClass().getParentClass();
    return (aClass != null) ? aClass.getMethod(this.getName()) : null;
  }

  @Override
  public String toString() {
    return "LogtalkMethodModel(" + this.getName() + ", " + this.getParameters() + ")";
  }

  @Override
  public List<LogtalkModel> getExposedMembers() {
    return null;
  };

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return getDeclaringClass();
  }

}

