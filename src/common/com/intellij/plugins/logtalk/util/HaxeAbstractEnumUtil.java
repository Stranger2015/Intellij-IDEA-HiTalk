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
import com.intellij.plugins.haxe.model.type.LogtalkClassReference;
import com.intellij.plugins.haxe.model.type.ResultHolder;
import com.intellij.plugins.haxe.model.type.SpecificLogtalkClassReference;
import com.intellij.plugins.logtalk.lang.psi.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Extensions for resolving and analyzing Logtalk @:enum abstract type
 */
public class LogtalkAbstractEnumUtil {

  @Contract("null -> false")
  public static boolean isAbstractEnum(@Nullable PsiClass clazz) {
    return clazz instanceof LogtalkClass && ((LogtalkClass)clazz).isAbstract() && clazz.isEnum();
  }

  /**
   * If this element suitable for processing by `@:enum abstract` logic
   * IMPORTANT: This method doesn't check if this field inside `@:enum abstract`!
   */
  @Contract("null -> false")
  public static boolean couldBeAbstractEnumField(@Nullable PsiElement element) {
    if (element != null && element instanceof LogtalkVarDeclaration) {
      final LogtalkVarDeclaration decl = (LogtalkVarDeclaration)element;
      if (decl.getPropertyDeclaration() == null && !decl.isStatic()) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  public static LogtalkClassResolveResult resolveFieldType(@Nullable PsiElement element) {
    final LogtalkClass cls = getFieldClass(element);
    return cls != null ? LogtalkClassResolveResult.create(cls) : null;
  }

  @Nullable
  public static ResultHolder getFieldType(@Nullable PsiElement element) {
    final LogtalkClass cls = getFieldClass(element);
    if (cls != null && element != null) {
      ResultHolder result = new ResultHolder(SpecificLogtalkClassReference.withoutGenerics(new LogtalkClassReference(cls.getModel(), element)));
      if (element instanceof LogtalkVarDeclaration) {
        final LogtalkVarInit init = ((LogtalkVarDeclaration)element).getVarInit();
        if (init != null && init.getExpression() != null) {
          result = result.withConstantValue(init.getExpression().getText());
        }
      }
      return result;
    }
    return null;
  }

  @Nullable
  @Contract("null -> null")
  public static ResultHolder getStaticMemberExpression(@Nullable PsiElement expression) {
    if (expression != null) {
      final PsiElement containerElement = expression.getFirstChild();
      final PsiElement memberElement = expression.getLastChild();

      if (containerElement instanceof LogtalkReference && memberElement instanceof LogtalkIdentifier) {
        final LogtalkClass leftClass = ((LogtalkReference)containerElement).resolveLogtalkClass().getLogtalkClass();
        if (isAbstractEnum(leftClass)) {
          final LogtalkNamedComponent enumField = leftClass.findLogtalkFieldByName(memberElement.getText());
          if (enumField != null) {
            ResultHolder result = getFieldType(enumField);
            if (result != null) {
              return result;
            }
          }
        }
      }
    }
    return null;
  }

  /*** HELPERS ***/

  @Nullable
  private static LogtalkClass getFieldClass(@Nullable PsiElement element) {
    final LogtalkVarDeclaration varDecl = element != null && (element instanceof LogtalkVarDeclaration) ?
                                       (LogtalkVarDeclaration)element : null;
    if (couldBeAbstractEnumField(varDecl)) {
      final LogtalkAbstractClassDeclaration abstractEnumClass =
        PsiTreeUtil.getParentOfType(varDecl, LogtalkAbstractClassDeclaration.class);
      if (isAbstractEnum(abstractEnumClass)) {
        if (varDecl.getTypeTag() == null) {
          return abstractEnumClass;
        }
      }
      LogtalkClassResolveResult result = LogtalkResolveUtil.tryResolveClassByTypeTag(varDecl, new LogtalkGenericSpecialization());
      if (result.getLogtalkClass() != null) {
        return result.getLogtalkClass();
      }
    }
    return null;
  }
}
