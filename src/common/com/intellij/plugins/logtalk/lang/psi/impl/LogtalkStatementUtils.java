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
package com.intellij.plugins.logtalk.lang.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

/**
 * Routines to help process statements and their contents.
 *
 * Created by ebishton on 10/17/14.
 */
public class LogtalkStatementUtils {

  // All static interface.
  private LogtalkStatementUtils() {};

  /**
   * Retrieve a list of child block statements.  May be nested more than one
   * level deep.
   */
  public static List<LogtalkBlockStatement> getBlockStatementList(PsiElement elem) {
    return PsiTreeUtil.getChildrenOfTypeAsList(elem, LogtalkBlockStatement.class);
  }

  /**
   * Retrieve the (first) block that belongs to the given element.
   */
  public static LogtalkBlockStatement getBlockStatement(PsiElement elem) {
    return PsiTreeUtil.getChildOfType(elem, LogtalkBlockStatement.class);
  }

  /**
   * Retrieve a list of child expression statements associated with the
   * given element.
   */
  public static List<LogtalkExpression> getExpressionList(PsiElement elem) {
    return PsiTreeUtil.getChildrenOfTypeAsList(elem, LogtalkExpression.class);
  }

  /**
   * Retrieve the (first) expression that belongs to the given element.
   */
  public static LogtalkExpression getExpression(PsiElement elem) {
    return PsiTreeUtil.getChildOfType(elem, LogtalkExpression.class);
  }

  /**
   * Retrieve the (first) catch statement that belongs to the given element.
   */
  public static LogtalkCatchStatement getCatchStatement(PsiElement elem) {
    return PsiTreeUtil.getChildOfType(elem, LogtalkCatchStatement.class);
  }

}
