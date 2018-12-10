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

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypeSets;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.haxe.model.LogtalkClassModel;
import com.intellij.plugins.haxe.model.LogtalkMethodModel;
import com.intellij.plugins.haxe.model.fixer.*;
import com.intellij.plugins.haxe.util.*;
import com.intellij.plugins.logtalk.lang.lexer.LogtalkTokenTypeSets;
import com.intellij.plugins.logtalk.lang.psi.LogtalkClass;
import com.intellij.plugins.logtalk.lang.psi.LogtalkMethod;
import com.intellij.plugins.logtalk.lang.psi.LogtalkReference;
import com.intellij.plugins.logtalk.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.logtalk.model.LogtalkClassModel;
import com.intellij.plugins.logtalk.model.LogtalkMethodModel;
import com.intellij.plugins.logtalk.util.*;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;

public class LogtalkExpressionEvaluator {
  static final LogtalkDebugLogger LOG = LogtalkDebugLogger.getLogger();
  static { LOG.setLevel(Level.INFO); }

  @NotNull
  static public LogtalkExpressionEvaluatorContext evaluate(PsiElement element, LogtalkExpressionEvaluatorContext context) {
    context.result = handle(element, context);
    return context;
  }

  @NotNull
  static private ResultHolder handle(final PsiElement element, final LogtalkExpressionEvaluatorContext context) {
    try {
      return _handle(element, context);
    }
    catch (NullPointerException e) {
      // Make sure that these get into the log, because the GeneralHighlightingPass swallows them.
      LOG.error("Error evaluating expression type for element " + element.toString(), e);
      throw e;
    }
    catch (ProcessCanceledException e) {
      // Don't log these, because they are common, but DON'T swallow them, either; it makes things unresponsive.
      throw e;
    }
    catch (Throwable t) {
      // XXX: Watch this.  If it happens a lot, then maybe we shouldn't log it unless in debug mode.
      LOG.warn("Error evaluating expression type for element " + element.toString(), t);
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }
  }

  @NotNull
  static private ResultHolder _handle(final PsiElement element, final LogtalkExpressionEvaluatorContext context) {
    if (element == null) {
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }
    LOG.debug("Handling element: " + element);
    if (element instanceof PsiCodeBlock) {
      context.beginScope();
      ResultHolder type = SpecificLogtalkClassReference.getUnknown(element).createHolder();
      boolean deadCode = false;
      for (PsiElement childElement : element.getChildren()) {
        type = handle(childElement, context);
        if (deadCode) {
          //context.addWarning(childElement, "Unreachable statement");
          context.addUnreachable(childElement);
        }
        if (childElement instanceof LogtalkReturnStatement) {
          deadCode = true;
        }
      }
      context.endScope();
      return type;
    }

    if (element instanceof LogtalkReturnStatement) {
      PsiElement[] children = element.getChildren();
      ResultHolder result = SpecificLogtalkClassReference.getVoid(element).createHolder();
      if (children.length >= 1) {
        result = handle(children[0], context);
      }
      context.addReturnType(result, element);
      return result;
    }

    if (element instanceof LogtalkIterable) {
      return handle(((LogtalkIterable)element).getExpression(), context);
    }

    if (element instanceof LogtalkForStatement) {
      final LogtalkComponentName name = ((LogtalkForStatement)element).getComponentName();
      final LogtalkIterable iterable = ((LogtalkForStatement)element).getIterable();
      final PsiElement body = element.getLastChild();
      context.beginScope();
      try {
        final SpecificTypeReference iterableValue = handle(iterable, context).getType();
        SpecificTypeReference type = iterableValue.getIterableElementType(iterableValue).getType();
        if (iterableValue.isConstant()) {
          final Object constant = iterableValue.getConstant();
          if (constant instanceof LogtalkRange) {
            type = type.withRangeConstraint((LogtalkRange)constant);
          }
        }
        if (name != null) {
          context.setLocal(name.getText(), new ResultHolder(type));
        }
        return handle(body, context);
      }
      finally {
        context.endScope();
      }
    }

    if (element instanceof LogtalkSwitchStatement) {
      // TODO: Evaluating result of switch statement should properly implemented
    }

    if (element instanceof LogtalkNewExpression) {
      ResultHolder typeHolder = LogtalkTypeResolver.getTypeFromType(((LogtalkNewExpression)element).getType());
      if (typeHolder.getType() instanceof SpecificLogtalkClassReference) {
        final LogtalkClassModel clazz = ((SpecificLogtalkClassReference)typeHolder.getType()).getLogtalkClassModel();
        if (clazz != null) {
          LogtalkMethodModel constructor = clazz.getConstructor();
          if (constructor == null) {
            context.addError(element, "Class " + clazz.getName() + " doesn't have a constructor", new LogtalkFixer("Create constructor") {
              @Override
              public void run() {
                // @TODO: Check arguments
                clazz.addMethod("new");
              }
            });
          }
          else {
            checkParameters(element, constructor, ((LogtalkNewExpression)element).getExpressionList(), context);
          }
        }
      }
      return typeHolder.duplicate();
    }

    if (element instanceof LogtalkThisExpression) {
      //PsiReference reference = element.getReference();
      //LogtalkClassResolveResult result = LogtalkResolveUtil.getLogtalkClassResolveResult(element);
      LogtalkClass ancestor = UsefulPsiTreeUtil.getAncestor(element, LogtalkClass.class);
      if (ancestor == null) return SpecificTypeReference.getDynamic(element).createHolder();
      LogtalkClassModel model = ancestor.getModel();
      if (model.isAbstract()) {
        LogtalkTypeOrAnonymous type = model.getUnderlyingType();
        if (type != null) {
          LogtalkClass aClass = LogtalkResolveUtil.tryResolveClassByQName(type);
          if (aClass != null) {
            return SpecificLogtalkClassReference.withoutGenerics(new LogtalkClassReference(aClass.getModel(), element), element).createHolder();
          }
        }
      }
      return SpecificLogtalkClassReference.primitive(ancestor.getQualifiedName(), element).createHolder();
    }

    if (element instanceof LogtalkIdentifier) {
      //PsiReference reference = element.getReference();
      ResultHolder holder = context.get(element.getText());

      if (holder == null) {
        context.addError(element, "Unknown variable", new LogtalkCreateLocalVariableFixer(element.getText(), element));

        return SpecificTypeReference.getDynamic(element).createHolder();
      }

      return holder;
    }

    if (element instanceof LogtalkCastExpression) {
      handle(((LogtalkCastExpression)element).getExpression(), context);
      LogtalkTypeOrAnonymous anonymous = getFirstItem(((LogtalkCastExpression)element).getTypeOrAnonymousList());
      if (anonymous != null) {
        return LogtalkTypeResolver.getTypeFromTypeOrAnonymous(anonymous);
      }
      else {
        return SpecificLogtalkClassReference.getUnknown(element).createHolder();
      }
    }

    if (element instanceof LogtalkWhileStatement) {
      List<LogtalkExpression> list = ((LogtalkWhileStatement)element).getExpressionList();
      SpecificTypeReference type = null;
      LogtalkExpression lastExpression = null;
      for (LogtalkExpression expression : list) {
        type = handle(expression, context).getType();
        lastExpression = expression;
      }
      if (type == null) {
        type = SpecificTypeReference.getDynamic(element);
      }
      if (!type.isBool() && lastExpression != null) {
        context.addError(
          lastExpression,
          "While expression must be boolean",
          new LogtalkCastFixer(lastExpression, type, SpecificLogtalkClassReference.getBool(element))
        );
      }

      PsiElement body = element.getLastChild();
      if (body != null) {
        //return SpecificLogtalkClassReference.createArray(result); // @TODO: Check this
        return handle(body, context);
      }

      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }

    if (element instanceof LogtalkLocalVarDeclarationList) {
      for (LogtalkLocalVarDeclaration part : ((LogtalkLocalVarDeclarationList)element).getLocalVarDeclarationList()) {
        handle(part, context);
      }
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }

    if (element instanceof LogtalkAssignExpression) {
      final PsiElement left = element.getFirstChild();
      final PsiElement right = element.getLastChild();
      if (left != null && right != null) {
        final ResultHolder leftResult = handle(left, context);
        final ResultHolder rightResult = handle(right, context);

        if (leftResult.isUnknown()) {
          leftResult.setType(rightResult.getType());
        }
        leftResult.removeConstant();

        final SpecificTypeReference leftValue = leftResult.getType();
        final SpecificTypeReference rightValue = rightResult.getType();

        //leftValue.mutateConstantValue(null);
        if (!leftResult.canAssign(rightResult)) {
          context.addError(element, "Can't assign " + rightValue + " to " + leftValue, new LogtalkCastFixer(right, rightValue, leftValue));
        }

        if (leftResult.isImmutable()) {
          context.addError(element, "Trying to change an immutable value");
        }

        return rightResult;
      }
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }

    if (element instanceof LogtalkLocalVarDeclaration) {
      final LogtalkComponentName name = ((LogtalkLocalVarDeclaration)element).getComponentName();
      final LogtalkVarInit init = ((LogtalkLocalVarDeclaration)element).getVarInit();
      final LogtalkTypeTag typeTag = ((LogtalkLocalVarDeclaration)element).getTypeTag();
      ResultHolder result = SpecificLogtalkClassReference.getUnknown(element).createHolder();
      if (init != null) {
        result = handle(init, context);
      }
      if (typeTag != null) {
        result = LogtalkTypeResolver.getTypeFromTypeTag(typeTag, element);
      }

      if (typeTag != null) {
        final ResultHolder tag = LogtalkTypeResolver.getTypeFromTypeTag(typeTag, element);
        if (!tag.canAssign(result)) {
          result = tag.duplicate();

          context.addError(
            element,
            "Can't assign " + result + " to " + tag,
            new LogtalkTypeTagChangeFixer(typeTag, result.getType()),
            new LogtalkTypeTagRemoveFixer(typeTag)
          );
        }
      }

      if (name != null) {
        context.setLocal(name.getText(), result);
      }

      return result;
    }

    if (element instanceof LogtalkVarInit) {
      return handle(((LogtalkVarInit)element).getExpression(), context);
    }

    if (element instanceof LogtalkReferenceExpression) {
      PsiElement[] children = element.getChildren();
      ResultHolder typeHolder = handle(children[0], context);
      boolean resolved = true;
      for (int n = 1; n < children.length; n++) {
        String accessName = children[n].getText();
        if (typeHolder.getType().isString() && typeHolder.getType().isConstant() && accessName.equals("code")) {
          String str = (String)typeHolder.getType().getConstant();
          typeHolder = SpecificTypeReference.getInt(element, (str != null && str.length() >= 1) ? str.charAt(0) : -1).createHolder();
          if (str == null || str.length() != 1) {
            context.addError(element, "String must be a single UTF8 char");
          }
        }
        else {
          ResultHolder access = typeHolder.getType().access(accessName, context);
          if (access == null) {
            resolved = false;
            Annotation annotation = context.addError(children[n], "Can't resolve '" + accessName + "' in " + typeHolder.getType());
            if (children.length == 1) {
              annotation.registerFix(new LogtalkCreateLocalVariableFixer(accessName, element));
            } else {
              annotation.registerFix(new LogtalkCreateMethodFixer(accessName, element));
              annotation.registerFix(new LogtalkCreateFieldFixer(accessName, element));
            }
          }
          typeHolder = access;
        }
      }

      // @TODO: this should be innecessary when code is working right!
      if (!resolved) {
        PsiReference reference = element.getReference();
        if (reference != null) {
          PsiElement subelement = reference.resolve();
          if (subelement instanceof AbstractLogtalkNamedComponent) {
            typeHolder = LogtalkTypeResolver.getFieldOrMethodReturnType((AbstractLogtalkNamedComponent)subelement);
          }
        }
      }

      return (typeHolder != null) ? typeHolder : SpecificTypeReference.getDynamic(element).createHolder();
    }

    if (element instanceof LogtalkCallExpression) {
      LogtalkCallExpression callelement = (LogtalkCallExpression)element;
      LogtalkExpression callLeft = ((LogtalkCallExpression)element).getExpression();
      SpecificTypeReference functionType = handle(callLeft, context).getType();

      // @TODO: this should be innecessary when code is working right!
      if (functionType.isUnknown()) {
        if (callLeft instanceof LogtalkReference) {
          PsiReference reference = callLeft.getReference();
          if (reference != null) {
            PsiElement subelement = reference.resolve();
            if (subelement instanceof LogtalkMethod) {
              functionType = ((LogtalkMethod)subelement).getModel().getFunctionType();
            }
          }
        }
      }

      if (functionType.isUnknown()) {
        LOG.debug("Couldn't resolve " + callLeft.getText());
      }

      List<LogtalkExpression> parameterExpressions = null;
      if (callelement.getExpressionList() != null) {
        parameterExpressions = callelement.getExpressionList().getExpressionList();
      }
      else {
        parameterExpressions = Collections.emptyList();
      }

      if (functionType instanceof SpecificFunctionReference) {
        SpecificFunctionReference ftype = (SpecificFunctionReference)functionType;
        LogtalkExpressionEvaluator.checkParameters(callelement, ftype, parameterExpressions, context);

        return ftype.getReturnType().duplicate();
      }

      if (functionType.isDynamic()) {
        for (LogtalkExpression expression : parameterExpressions) {
          handle(expression, context);
        }

        return functionType.withoutConstantValue().createHolder();
      }

      // @TODO: resolve the function type return type
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }

    if (element instanceof LogtalkLiteralExpression) {
      return handle(element.getFirstChild(), context);
    }

    if (element instanceof LogtalkStringLiteralExpression) {
      // @TODO: check if it has string interpolation inside, in that case text is not constant
      return SpecificLogtalkClassReference.primitive(
        "String",
        element,
        LogtalkStringUtil.unescapeString(element.getText())
      ).createHolder();
    }

    if (element instanceof LogtalkExpressionList) {
      ArrayList<ResultHolder> references = new ArrayList<ResultHolder>();
      for (LogtalkExpression expression : ((LogtalkExpressionList)element).getExpressionList()) {
        references.add(handle(expression, context));
      }
      return LogtalkTypeUnifier.unifyHolders(references, element);
    }

    if (element instanceof LogtalkMapLiteral) {
      LogtalkMapInitializerExpressionList listElement = ((LogtalkMapLiteral)element).getMapInitializerExpressionList();
      List<LogtalkExpression> initializers = new ArrayList<>();

      // In maps, comprehensions don't have expression lists, but they do have one single initializer.
      if (null == listElement) {
        LogtalkMapInitializerForStatement forStatement = ((LogtalkMapLiteral)element).getMapInitializerForStatement();
        LogtalkMapInitializerWhileStatement whileStatement = ((LogtalkMapLiteral)element).getMapInitializerWhileStatement();
        LogtalkExpression fatArrow = null;
        while (null != forStatement || null != whileStatement) {
          if (null != forStatement) {
            fatArrow = forStatement.getMapInitializer();
            whileStatement = forStatement.getMapInitializerWhileStatement();
            forStatement = forStatement.getMapInitializerForStatement();
          } else {
            fatArrow = whileStatement.getMapInitializer();
            forStatement = whileStatement.getMapInitializerForStatement();
            whileStatement = whileStatement.getMapInitializerWhileStatement();
          }
        }
        if (null != fatArrow) {
          initializers.add(fatArrow);
        } else {
          LOG.error("Didn't find an initializer in a map comprehension: " + element.toString(),
                    new LogtalkDebugUtil.InvalidValueException(element.toString() + '\n' + LogtalkDebugUtil.elementLocation(element)));
        }
      } else {
        initializers.addAll(listElement.getExpressionList());
      }

      ArrayList<SpecificTypeReference> keyReferences = new ArrayList<>(initializers.size());
      ArrayList<SpecificTypeReference> valueReferences = new ArrayList<>(initializers.size());
      for (LogtalkExpression ex : initializers) {
        LogtalkFatArrowExpression fatArrow = (LogtalkFatArrowExpression)ex;
        SpecificTypeReference keyType = handle(fatArrow.getFirstChild(), context).getType();
        keyReferences.add(keyType);
        SpecificTypeReference valueType = handle(fatArrow.getLastChild(), context).getType();
        valueReferences.add(valueType);
      }

      // XXX: Maybe track and add constants to the type references, like arrays do??
      //      That has implications on how they're displayed (e.g. not as key=>value,
      //      but as separate arrays).
      ResultHolder keyTypeHolder = LogtalkTypeUnifier.unify(keyReferences, element).withoutConstantValue().createHolder();
      ResultHolder valueTypeHolder = LogtalkTypeUnifier.unify(valueReferences, element).withoutConstantValue().createHolder();

      SpecificTypeReference result = SpecificLogtalkClassReference.createMap(keyTypeHolder, valueTypeHolder);
      ResultHolder holder = result.createHolder();
      return holder;
    } // end LogtalkMapLiteral

    if (element instanceof LogtalkArrayLiteral) {
      LogtalkExpressionList list = ((LogtalkArrayLiteral)element).getExpressionList();

      // Check if it's a comprehension.
      if (list != null) {
        final List<LogtalkExpression> list1 = list.getExpressionList();
        if (list1.isEmpty()) {
          final PsiElement child = list.getFirstChild();
          if ((child instanceof LogtalkForStatement) || (child instanceof LogtalkWhileStatement)) {
            return SpecificTypeReference.createArray(handle(child, context)).createHolder();
          }
        }
      }

      ArrayList<SpecificTypeReference> references = new ArrayList<SpecificTypeReference>();
      ArrayList<Object> constants = new ArrayList<Object>();
      boolean allConstants = true;
      if (list != null) {
        for (LogtalkExpression expression : list.getExpressionList()) {
          SpecificTypeReference type = handle(expression, context).getType();
          if (!type.isConstant()) {
            allConstants = false;
          }
          else {
            constants.add(type.getConstant());
          }
          references.add(type);
        }
      }

      ResultHolder elementTypeHolder = references.isEmpty()
                                       ? SpecificTypeReference.getUnknown(element).createHolder()
                                       : LogtalkTypeUnifier.unify(references, element).withoutConstantValue().createHolder();

      SpecificTypeReference result = SpecificLogtalkClassReference.createArray(elementTypeHolder);
      if (allConstants) result = result.withConstantValue(constants);
      ResultHolder holder = result.createHolder();
      return holder;
    }

    if (element instanceof PsiJavaToken) {
      IElementType type = ((PsiJavaToken)element).getTokenType();

      if (type == LogtalkTokenTypes.LITINT || type == LogtalkTokenTypes.LITHEX || type == LogtalkTokenTypes.LITOCT) {
        return SpecificLogtalkClassReference.primitive("Int", element, Long.decode(element.getText())).createHolder();
      }
      else if (type == LogtalkTokenTypes.LITFLOAT) {
        return SpecificLogtalkClassReference.primitive("Float", element, Double.parseDouble(element.getText())).createHolder();
      }
      else if (type == LogtalkTokenTypes.KFALSE || type == LogtalkTokenTypes.KTRUE) {
        return SpecificLogtalkClassReference.primitive("Bool", element, type == LogtalkTokenTypes.KTRUE).createHolder();
      }
      else if (type == LogtalkTokenTypes.KNULL) {
        return SpecificLogtalkClassReference.primitive("Dynamic", element, LogtalkNull.instance).createHolder();
      }
      else {
        LOG.debug("Unhandled token type: " + type);
        return SpecificLogtalkClassReference.getDynamic(element).createHolder();
      }
    }

    if (element instanceof LogtalkSuperExpression) {
      /*
      LOG.debug("-------------------------");
      final LogtalkExpressionList list = LogtalkPsiUtils.getChildWithText(element, LogtalkExpressionList.class);
      LOG.debug(element);
      LOG.debug(list);
      final List<LogtalkExpression> parameters = (list != null) ? list.getExpressionList() : Collections.<LogtalkExpression>emptyList();
      final LogtalkMethodModel method = LogtalkJavaUtil.cast(LogtalkMethodModel.fromPsi(element), LogtalkMethodModel.class);
      if (method == null) {
        context.addError(element, "Not in a method");
      }
      if (method != null) {
        final LogtalkMethodModel parentMethod = method.getParentMethod();
        if (parentMethod == null) {
          context.addError(element, "Calling super without parent constructor");
        } else {
          LOG.debug(element);
          LOG.debug(parentMethod.getFunctionType());
          LOG.debug(parameters);
          checkParameters(element, parentMethod.getFunctionType(), parameters, context);
          //LOG.debug(method);
          //LOG.debug(parentMethod);
        }
      }
      return SpecificLogtalkClassReference.getVoid(element);
      */
      final LogtalkMethodModel method = LogtalkJavaUtil.cast(LogtalkMethodModel.fromPsi(element), LogtalkMethodModel.class);
      final LogtalkMethodModel parentMethod = (method != null) ? method.getParentMethod() : null;
      if (parentMethod != null) {
        return parentMethod.getFunctionType().createHolder();
      }
      context.addError(element, "Calling super without parent constructor");
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }

    if (element instanceof LogtalkIteratorExpression) {
      final List<LogtalkExpression> list = ((LogtalkIteratorExpression)element).getExpressionList();
      if (list.size() >= 2) {
        final SpecificTypeReference left = handle(list.get(0), context).getType();
        final SpecificTypeReference right = handle(list.get(1), context).getType();
        Object constant = null;
        if (left.isConstant() && right.isConstant()) {
          constant = new LogtalkRange(
            LogtalkTypeUtils.getIntValue(left.getConstant()),
            LogtalkTypeUtils.getIntValue(right.getConstant())
          );
        }
        return SpecificLogtalkClassReference.getIterator(SpecificLogtalkClassReference.getInt(element)).withConstantValue(constant)
          .createHolder();
      }
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }

    if (element instanceof LogtalkArrayAccessExpression) {
      final List<LogtalkExpression> list = ((LogtalkArrayAccessExpression)element).getExpressionList();
      if (list.size() >= 2) {
        final SpecificTypeReference left = handle(list.get(0), context).getType();
        final SpecificTypeReference right = handle(list.get(1), context).getType();
        if (left.isArray()) {
          Object constant = null;
          if (left.isConstant()) {
            List array = (List)left.getConstant();
            final LogtalkRange constraint = right.getRangeConstraint();
            LogtalkRange arrayBounds = new LogtalkRange(0, array.size());
            if (right.isConstant()) {
              final int index = LogtalkTypeUtils.getIntValue(right.getConstant());
              if (arrayBounds.contains(index)) {
                constant = array.get(index);
              }
              else {
                context.addWarning(element, "Out of bounds " + index + " not inside " + arrayBounds);
              }
            }
            else if (constraint != null) {
              if (!arrayBounds.contains(constraint)) {
                context.addWarning(element, "Out of bounds " + constraint + " not inside " + arrayBounds);
              }
            }
          }
          return left.getArrayElementType().getType().withConstantValue(constant).createHolder();
        }
      }
      return SpecificLogtalkClassReference.getUnknown(element).createHolder();
    }

    if (element instanceof LogtalkFunctionLiteral) {
      LogtalkFunctionLiteral function = (LogtalkFunctionLiteral)element;
      LogtalkParameterList params = function.getParameterList();
      if (params == null) {
        return SpecificLogtalkClassReference.getInvalid(function).createHolder();
      }
      LinkedList<ResultHolder> results = new LinkedList<ResultHolder>();
      ResultHolder returnType = null;
      context.beginScope();
      try {
        if (params instanceof LogtalkOpenParameterList) {
          // Arrow function with a single, unparenthesized, parameter.
          LogtalkOpenParameterList openParamList = ((LogtalkOpenParameterList)params);

          // TODO: Infer the type from first usage in the function body.
          ResultHolder vartype = SpecificTypeReference.getUnknown(function).createHolder();

          context.setLocal(openParamList.getComponentName().getName(), vartype);
          results.add(vartype);
        } else {
          for (LogtalkParameter parameter : params.getParameterList()) {
            ResultHolder vartype = LogtalkTypeResolver.getTypeFromTypeTag(parameter.getTypeTag(), function);
            String name = parameter.getName();
            if (name != null) {
              context.setLocal(name, vartype);
            }
            results.add(vartype);
          }
        }
        context.addLambda(context.createChild(function.getLastChild()));
        LogtalkTypeTag tag = (function.getTypeTag());
        if (null != tag) {
          returnType = LogtalkTypeResolver.getTypeFromTypeTag(tag, function);
        } else {
          // If there was no type tag on the function, then we try to infer the value:
          // If there is a block to this method, then return the type of the block.  (See PsiBlockStatement above.)
          // If there is not a block, but there is an expression, then return the type of that expression.
          // If there is not a block, but there is a statement, then return the type of that statement.
          LogtalkBlockStatement block = function.getBlockStatement();
          if (null != block) {
            returnType = handle(block, context);
          } else if (null != function.getExpression()) {
            returnType = handle(function.getExpression(), context);
          } else {
            // Only one of these can be non-null at a time.
            PsiElement possibleStatements[] = { function.getDoWhileStatement(), function.getForStatement(), function.getIfStatement(),
                function.getReturnStatement(), function.getThrowStatement(), function.getWhileStatement() };
            for (PsiElement statement : possibleStatements) {
              if (null != statement) {
                returnType = handle(statement, context);
              }
            }
          }
        }
      } finally {
        context.endScope();
      }

      return new SpecificFunctionReference(results, returnType, null, function).createHolder();
    }

    if (element instanceof LogtalkIfStatement) {
      PsiElement[] children = element.getChildren();
      if (children.length >= 1) {
        SpecificTypeReference expr = handle(children[0], context).getType();
        if (!SpecificTypeReference.getBool(element).canAssign(expr)) {
          context.addError(
            children[0],
            "If expr " + expr + " should be bool",
            new LogtalkCastFixer(children[0], expr, SpecificLogtalkClassReference.getBool(element))
          );
        }

        if (expr.isConstant()) {
          context.addWarning(children[0], "If expression constant");
        }

        if (children.length < 2) return SpecificLogtalkClassReference.getUnknown(element).createHolder();
        PsiElement eTrue = null;
        PsiElement eFalse = null;
        eTrue = children[1];
        if (children.length >= 3) {
          eFalse = children[2];
        }
        SpecificTypeReference tTrue = null;
        SpecificTypeReference tFalse = null;
        if (eTrue != null) tTrue = handle(eTrue, context).getType();
        if (eFalse != null) tFalse = handle(eFalse, context).getType();
        if (expr.isConstant()) {
          if (expr.getConstantAsBool()) {
            if (tFalse != null) {
              context.addUnreachable(eFalse);
            }
          }
          else {
            if (tTrue != null) {
              context.addUnreachable(eTrue);
            }
          }
        }
        return LogtalkTypeUnifier.unify(tTrue, tFalse, element).createHolder();
      }
    }

    if (element instanceof LogtalkParenthesizedExpression) {
      return handle(element.getChildren()[0], context);
    }

    if (element instanceof LogtalkTernaryExpression) {
      LogtalkExpression[] list = ((LogtalkTernaryExpression)element).getExpressionList().toArray(new LogtalkExpression[0]);
      return LogtalkTypeUnifier.unify(handle(list[1], context).getType(), handle(list[2], context).getType(), element).createHolder();
    }

    if (element instanceof LogtalkPrefixExpression) {
      LogtalkExpression expression = ((LogtalkPrefixExpression)element).getExpression();
      if (expression == null) {
        return handle(element.getFirstChild(), context);
      }
      else {
        ResultHolder typeHolder = handle(expression, context);
        SpecificTypeReference type = typeHolder.getType();
        if (type.getConstant() != null) {
          String operatorText = getOperator(element, LogtalkTokenTypeSets.OPERATORS);
          return type.withConstantValue(LogtalkTypeUtils.applyUnaryOperator(type.getConstant(), operatorText)).createHolder();
        }
        return typeHolder;
      }
    }

    if (
      (element instanceof LogtalkAdditiveExpression) ||
      (element instanceof LogtalkBitwiseExpression) ||
      (element instanceof LogtalkShiftExpression) ||
      (element instanceof LogtalkLogicAndExpression) ||
      (element instanceof LogtalkLogicOrExpression) ||
      (element instanceof LogtalkCompareExpression) ||
      (element instanceof LogtalkMultiplicativeExpression)
      ) {
      PsiElement[] children = element.getChildren();
      String operatorText;
      if (children.length == 3) {
        operatorText = children[1].getText();
        return LogtalkOperatorResolver.getBinaryOperatorResult(
          element, handle(children[0], context).getType(), handle(children[2], context).getType(),
          operatorText, context
        ).createHolder();
      }
      else {
        operatorText = getOperator(element, LogtalkTokenTypeSets.OPERATORS);
        return LogtalkOperatorResolver.getBinaryOperatorResult(
          element, handle(children[0], context).getType(), handle(children[1], context).getType(),
          operatorText, context
        ).createHolder();
      }
    }

    LOG.debug("Unhandled " + element.getClass());
    return SpecificLogtalkClassReference.getDynamic(element).createHolder();
  }

  static private void checkParameters(
    final PsiElement callelement,
    final LogtalkMethodModel method,
    final List<LogtalkExpression> arguments,
    final LogtalkExpressionEvaluatorContext context
  ) {
    checkParameters(callelement, method.getFunctionType(), arguments, context);
  }

  static private void checkParameters(
    PsiElement callelement,
    SpecificFunctionReference ftype,
    List<LogtalkExpression> parameterExpressions,
    LogtalkExpressionEvaluatorContext context
  ) {
    List<ResultHolder> parameterTypes = ftype.getParameters();
    int len = Math.min(parameterTypes.size(), parameterExpressions.size());
    for (int n = 0; n < len; n++) {
      ResultHolder type = parameterTypes.get(n);
      LogtalkExpression expression = parameterExpressions.get(n);
      ResultHolder value = handle(expression, context);

      if (!type.canAssign(value)) {
        context.addError(
          expression,
          "Can't assign " + value + " to " + type,
          new LogtalkCastFixer(expression, value.getType(), type.getType())
        );
      }
    }

    //LOG.debug(ftype.getDebugString());
    // More parameters than expected
    if (parameterExpressions.size() > parameterTypes.size()) {
      for (int n = parameterTypes.size(); n < parameterExpressions.size(); n++) {
        context.addError(parameterExpressions.get(n), "Unexpected argument");
      }
    }
    // Less parameters than expected
    else if (parameterExpressions.size() < ftype.getNonOptionalArgumentsCount()) {
      context.addError(callelement, "Less arguments than expected");
    }
  }

  static private String getOperator(PsiElement element, TokenSet set) {
    ASTNode operatorNode = element.getNode().findChildByType(set);
    if (operatorNode == null) return "";
    return operatorNode.getText();
  }
}
