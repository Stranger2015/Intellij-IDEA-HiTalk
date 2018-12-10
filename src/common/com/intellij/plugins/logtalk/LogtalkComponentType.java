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
package com.intellij.plugins.logtalk;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author: Fedor.Korotkov
 */
public enum LogtalkComponentType {
  CLASS(0) {
    @Override
    public Icon getIcon() {
      return icons.LogtalkIcons.C_Logtalk;
    }
    @Override
    public Icon getCompletionIcon() {
      return AllIcons.Nodes.Class;
    }
  }, ENUM(1) {
    @Override
    public Icon getIcon() {
      return icons.LogtalkIcons.E_Logtalk;
    }
    @Override
    public Icon getCompletionIcon() {
      return AllIcons.Nodes.Enum;
    }
  }, INTERFACE(2) {
    @Override
    public Icon getIcon() {
      return icons.LogtalkIcons.I_Logtalk;
    }
    @Override
    public Icon getCompletionIcon() {
      return AllIcons.Nodes.Interface;
    }
  }, FUNCTION(3) {
    @Override
    public Icon getIcon() {
      return AllIcons.Nodes.Function;
    }
  }, METHOD(4) {
    @Override
    public Icon getIcon() {
      return AllIcons.Nodes.Method;
    }
  }, VARIABLE(5) {
    @Override
    public Icon getIcon() {
      return AllIcons.Nodes.Variable;
    }
  }, FIELD(6) {
    @Override
    public Icon getIcon() {
      return AllIcons.Nodes.Field;
    }
  }, PARAMETER(7) {
    @Override
    public Icon getIcon() {
      return AllIcons.Nodes.Parameter;
    }
  }, TYPEDEF(8) {
    @Override
    public Icon getIcon() {
      return AllIcons.Nodes.Annotationtype;
    }
  }, CLASSVARIABLE(9) {
    @Override
    public Icon getIcon() {
      return AllIcons.Nodes.Field;
    }
  };

  private final int myKey;

  LogtalkComponentType(int key) {
    myKey = key;
  }

  public int getKey() {
    return myKey;
  }

  public abstract Icon getIcon();

  public Icon getCompletionIcon() {
    return getIcon();
  }

  public static boolean isVariable(@Nullable LogtalkComponentType type) {
    return type == VARIABLE || type == PARAMETER || type == FIELD;
  }


  @Nullable
  public static LogtalkComponentType valueOf(int key) {
    switch (key) {
      case 0:
        return CLASS;//object
      case 1:
        return ENUM;
      case 2:
        return INTERFACE;//protocol
      case 3:
        return FUNCTION;//prolog clause
      case 4:
        return METHOD;  //entities' clause
      case 5:
        return VARIABLE; //entities' clauses/1
      case 6:
        return FIELD;
      case 7:
        return PARAMETER;
      case 8:
        return TYPEDEF;
      case 9:
        return CLASSVARIABLE;
    }
    return null;
  }


  @Nullable
  public static LogtalkComponentType typeOf(PsiElement element) {
    if (element instanceof LogtalkEnumDeclaration) {
      return ENUM;
    }
    if (element instanceof LogtalkInterfaceDeclaration  ) {
      return INTERFACE;
  }

    if (element instanceof LogtalkPredicateDeclaration){
      return METHOD;
    }

    if (element instanceof LogtalkLocalPredicateDeclaration ||
        element instanceof LogtalkFunctionLiteral) {
      return (LogtalkPredicateDeclaration) FUNCTION;
    }
    if (element instanceof LogtalkVarDeclaration ||
        element instanceof LogtalkEnumValueDeclaration ||
        element instanceof LogtalkAnonymousTypeField) {
      return FIELD;
    }
    if (element instanceof LogtalkLocalVarDeclaration0) {

      return VARIABLE;
    }
    if (element instanceof LogtalkParameter) {
      return PARAMETER;
    }
    if (element instanceof LogtalkVarDeclaration) {
      return CLASSVARIABLE;
    }

    return null;
  }

  @Nullable
  public static String getName(PsiElement element) {
    final LogtalkComponentType type = typeOf(element);
    if (type == null) {
      return null;
    }
    return type.toString().toLowerCase();
  }

  @Nullable
  public static String getPresentableName(PsiElement element) {
    final LogtalkComponentType type = typeOf(element);
    if (type == null) {
      return null;
    }
    switch (type) {
      case TYPEDEF:
      case CLASS:
      case ENUM:
      case INTERFACE:
            return ((LogtalkClass) element).getQualifiedName();
      case FUNCTION:
      case METHOD:
      case FIELD:
      case VARIABLE:
      case PARAMETER:
            return ((LogtalkNamedComponent) element).getName();
      default:
            return null;
    }
  }
}
