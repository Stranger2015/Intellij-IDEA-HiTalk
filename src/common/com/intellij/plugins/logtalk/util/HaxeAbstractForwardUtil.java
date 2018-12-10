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
package com.intellij.plugins.logtalk.util;

import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.LogtalkAbstractClassModel;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkNamedComponent;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Extensions for resolving and analyzing @:forward abstract meta
 */
public class LogtalkAbstractForwardUtil {

  public static boolean isElementInForwardMeta(@Nullable PsiElement element) {
    if (element != null) {
      if (element instanceof LogtalkCustomMeta) {
        return element.getText().contains("@:forward(");
      }
      return isElementInForwardMeta(element.getParent());
    }
    return false;
  }

  @Nullable
  public static List<LogtalkNamedComponent> findAbstractForwardingNamedSubComponents(@Nullable LogtalkClass clazz) {
    final List<String> forwardingFieldsNames = getAbstractForwardingFieldsNames(clazz);
    if (forwardingFieldsNames != null && clazz instanceof LogtalkAbstractClassDeclaration) {
      final LogtalkAbstractClassModel abstractClassModel = (LogtalkAbstractClassModel)clazz.getModel();
      final LogtalkClass underlyingClass = abstractClassModel.getUnderlyingClass();
      if (underlyingClass != null) {
        if (forwardingFieldsNames.isEmpty()) {
          return LogtalkResolveUtil.findNamedSubComponents(underlyingClass);
        }
        List<LogtalkNamedComponent> haxeNamedComponentList = new ArrayList<>();
        for (String fieldName : forwardingFieldsNames) {
          LogtalkNamedComponent component = LogtalkResolveUtil.findNamedSubComponent(underlyingClass, fieldName);
          if (component != null) {
            haxeNamedComponentList.add(component);
          }
        }
        return haxeNamedComponentList;
      }
    }
    return null;
  }

  @Nullable
  public static List<String> getAbstractForwardingFieldsNames(@Nullable LogtalkClass clazz) {
    if (clazz == null) return null;
    List<String> forwardingFields = new LinkedList<>();
    LogtalkMacroClass meta = clazz.getMeta("@:forward");
    if (meta != null) {
      LogtalkCustomMeta customMeta = meta.getCustomMeta();
      if (customMeta != null) {
        LogtalkExpressionList expressions = customMeta.getExpressionList();
        if (expressions != null) {
          for (LogtalkExpression expr : expressions.getExpressionList()) {
            final String name = expr.getText();
            if (name != null && !name.isEmpty() && !forwardingFields.contains(name)) {
              forwardingFields.add(name);
            }
          }
        }
        return forwardingFields;
      }
    }
    return null;
  }
}
