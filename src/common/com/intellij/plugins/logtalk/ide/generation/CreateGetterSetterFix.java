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
package com.intellij.plugins.logtalk.ide.generation;

import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.LogtalkFieldModel;
import com.intellij.plugins.haxe.util.LogtalkElementGenerator;
import com.intellij.plugins.haxe.util.LogtalkPresentableUtil;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public class CreateGetterSetterFix extends BaseCreateMethodsFix {
  public static enum Strategy {
    GETTER {
      @Override
      boolean accept(String name, Set<String> names) {
        return !names.contains(LogtalkPresentableUtil.getterName(name));
      }
    }, SETTER {
      @Override
      boolean accept(String name, Set<String> names) {
        return !names.contains(LogtalkPresentableUtil.setterName(name));
      }
    }, GETTERSETTER {
      @Override
      boolean accept(String name, Set<String> names) {
        return !names.contains(LogtalkPresentableUtil.getterName(name)) ||
               !names.contains(LogtalkPresentableUtil.setterName(name));
      }
    };

    abstract boolean accept(String name, Set<String> names);
  }

  private final Strategy myStratagy;

  public CreateGetterSetterFix(final LogtalkClass haxeClass, Strategy strategy) {
    super(haxeClass);
    myStratagy = strategy;
  }

  @Override
  protected String buildFunctionsText(LogtalkNamedComponent namedComponent) {
    if (!(namedComponent instanceof LogtalkVarDeclaration)) {
      return "";
    }

    LogtalkFieldModel field = new LogtalkFieldModel((LogtalkVarDeclaration)namedComponent);
    final StringBuilder result = new StringBuilder();
    if (myStratagy == Strategy.GETTER || myStratagy == Strategy.GETTERSETTER) {
      LogtalkNamedComponent getterMethod = myLogtalkClass.findLogtalkMethodByName(LogtalkPresentableUtil.getterName(field.getName()));
      if (getterMethod == null) {
        GetterSetterMethodBuilder.buildGetter(result, field);
      }
    }
    if (myStratagy == Strategy.SETTER || myStratagy == Strategy.GETTERSETTER) {
      LogtalkNamedComponent setterMethod = myLogtalkClass.findLogtalkMethodByName(LogtalkPresentableUtil.setterName(field.getName()));
      if (setterMethod == null) {
        GetterSetterMethodBuilder.buildSetter(result, field);
      }
    }
    return result.toString();
  }

  @Override
  protected void modifyElement(LogtalkNamedComponent namedComponent) {
    if (!(namedComponent instanceof LogtalkVarDeclaration)) {
      return;
    }
    if (((LogtalkVarDeclaration)namedComponent).getPropertyDeclaration() != null) {
      // todo: modify
      return;
    }

    final String typeText = LogtalkPresentableUtil.buildTypeText(namedComponent, ((LogtalkVarDeclaration)namedComponent).getTypeTag());

    final LogtalkVarDeclaration declaration =
      LogtalkElementGenerator.createVarDeclaration(namedComponent.getProject(), buildVarDeclaration(namedComponent, typeText));
    final LogtalkPropertyDeclaration propertyDeclaration = declaration.getPropertyDeclaration();
    if (propertyDeclaration != null) {
      LogtalkVarDeclaration varDeclaration = PsiTreeUtil.getParentOfType(namedComponent, LogtalkVarDeclaration.class, false);
      if (varDeclaration != null) {
        varDeclaration.replace(declaration);
      }
    }
  }

  private String buildVarDeclaration(LogtalkNamedComponent namedComponent, String typeText) {
    final StringBuilder result = new StringBuilder();
    result.append("@:isVar ");
    if (namedComponent.isPublic()) {
      result.append("public ");
    }
    if (namedComponent.isStatic()) {
      result.append("static ");
    }
    result.append("var ");
    result.append(namedComponent.getName());
    result.append("(");
    if (myStratagy == Strategy.GETTER || myStratagy == Strategy.GETTERSETTER) {
      result.append("get");
    }
    else {
      result.append("null");
    }
    result.append(",");
    if (myStratagy == Strategy.SETTER || myStratagy == Strategy.GETTERSETTER) {
      result.append("set");
    }
    else {
      result.append("null");
    }
    result.append(")");

    if (!typeText.isEmpty()) {
      result.append(":" + typeText);
    }

    result.append(";");
    return result.toString();
  }
}
