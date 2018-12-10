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

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.haxe.lang.psi.impl.LogtalkMethodImpl;
import com.intellij.plugins.haxe.util.LogtalkAbstractEnumUtil;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.lang.psi.LogtalkMethod;
import com.intellij.plugins.logtalk.lang.psi.LogtalkPsiModifier;
import com.intellij.plugins.logtalk.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.logtalk.lang.psi.impl.LogtalkMethodImpl;
import com.intellij.plugins.logtalk.util.LogtalkAbstractEnumUtil;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;

public class LogtalkTypeResolver {
  @NotNull
  static public ResultHolder getFieldOrMethodReturnType(@NotNull AbstractLogtalkNamedComponent comp) {
    return getFieldOrMethodReturnType(comp, null);
  }

  // @TODO: Check if cache works
  @NotNull
  static public ResultHolder getFieldOrMethodReturnType(@NotNull AbstractLogtalkNamedComponent comp, @Nullable LogtalkGenericResolver resolver) {
    // @TODO: cache should check if any related type has changed, which return depends
    if (comp.getContainingFile() == null) {
      return SpecificLogtalkClassReference.getUnknown(comp).createHolder();
    }
    long stamp = comp.getContainingFile().getModificationStamp();
    if (comp._cachedType == null || comp._cachedTypeStamp != stamp) {
      comp._cachedType = _getFieldOrMethodReturnType(comp, resolver);
      comp._cachedTypeStamp = stamp;
    }

    return comp._cachedType;
  }

  @NotNull
  static public ResultHolder getMethodFunctionType(PsiElement comp, @Nullable LogtalkGenericResolver resolver) {
    if (comp instanceof LogtalkMethod) {
      return ((LogtalkMethod)comp).getModel().getFunctionType(resolver).createHolder();
    }
    // @TODO: error
    return SpecificTypeReference.getInvalid(comp).createHolder();
  }

  @NotNull
  static private ResultHolder _getFieldOrMethodReturnType(AbstractLogtalkNamedComponent comp, @Nullable LogtalkGenericResolver resolver) {
    try {
      if (comp instanceof PsiMethod) {
        return getFunctionReturnType(comp);
      } else if (comp instanceof LogtalkFunctionLiteral) {
        return getFunctionReturnType(comp);
      } else if (comp instanceof LogtalkEnumValueDeclaration) {
        return getEnumReturnType((LogtalkEnumValueDeclaration)comp);
      } else {
        return getFieldType(comp);
      }
    }
    catch (Throwable e) {
      e.printStackTrace();
      return SpecificTypeReference.getUnknown(comp).createHolder();
    }
  }

  private static ResultHolder getEnumReturnType(LogtalkEnumValueDeclaration comp) {
    return getTypeFromTypeTag(comp.getReturnType(), comp.getParent());
  }

  @NotNull
  static private ResultHolder getFieldType(AbstractLogtalkNamedComponent comp) {
    //ResultHolder type = getTypeFromTypeTag(comp);
    // Here detect assignment
    final ResultHolder abstractEnumType = LogtalkAbstractEnumUtil.getFieldType(comp);
    if (abstractEnumType != null) {
      return abstractEnumType;
    }

    if (comp instanceof LogtalkVarDeclaration) {
      ResultHolder result = null;

      LogtalkVarDeclaration decl = (LogtalkVarDeclaration)comp;
      LogtalkVarInit init = ((LogtalkVarDeclaration)comp).getVarInit();
      if (init != null) {
        PsiElement child = init.getExpression();
        final ResultHolder initType = LogtalkTypeResolver.getPsiElementType(child);
        boolean isConstant = decl.hasModifierProperty(LogtalkPsiModifier.INLINE) && decl.isStatic();
        result = isConstant ? initType : initType.withConstantValue(null);
      }

      LogtalkTypeTag typeTag = ((LogtalkVarDeclaration)comp).getTypeTag();
      if (typeTag != null) {
        final ResultHolder typeFromTag = getTypeFromTypeTag(typeTag, comp);
        final Object initConstant = result != null ? result.getType().getConstant() : null;
        result = typeFromTag.withConstantValue(initConstant);
      }

      if (result != null) {
        return result;
      }
    }

    return SpecificTypeReference.getUnknown(comp).createHolder();
  }

  @NotNull
  static private ResultHolder getFunctionReturnType(AbstractLogtalkNamedComponent comp) {
    if (comp instanceof LogtalkMethodImpl) {
      LogtalkTypeTag typeTag = ((LogtalkMethodImpl)comp).getTypeTag();
      if (typeTag != null) {
        return getTypeFromTypeTag(typeTag, comp);
      }
    }
    if (comp instanceof LogtalkMethod) {
      final LogtalkExpressionEvaluatorContext context = getPsiElementType(((LogtalkMethod)comp).getModel().getBodyPsi(), null);
      return context.getReturnType();
    } else if (comp instanceof LogtalkFunctionLiteral) {
      final LogtalkExpressionEvaluatorContext context = getPsiElementType(comp.getLastChild(), null);
      return context.getReturnType();
    } else {
      throw new RuntimeException("Can't get the body of a no PsiMethod");
    }
  }

  @NotNull
  static public ResultHolder getTypeFromTypeTag(@Nullable final LogtalkTypeTag typeTag, @NotNull PsiElement context) {
    if (typeTag != null) {
      final LogtalkTypeOrAnonymous typeOrAnonymous = getFirstItem(typeTag.getTypeOrAnonymousList());
      final LogtalkFunctionType functionType = getFirstItem(typeTag.getFunctionTypeList());

      if (typeOrAnonymous != null) {
        return getTypeFromTypeOrAnonymous(typeOrAnonymous);
      }

      //comp.getContainingFile().getNode().putUserData();

      if (functionType != null) {
        return getTypeFromFunctionType(functionType);
      }
    }

    return SpecificTypeReference.getUnknown(context).createHolder();
  }

  @NotNull
  static public ResultHolder getTypeFromTypeTag(AbstractLogtalkNamedComponent comp, @NotNull PsiElement context) {
    return getTypeFromTypeTag(PsiTreeUtil.getChildOfType(comp, LogtalkTypeTag.class), context);
  }

  @NotNull
  static public ResultHolder getTypeFromFunctionType(LogtalkFunctionType type) {
    ArrayList<ResultHolder> args = new ArrayList<ResultHolder>();
    for (LogtalkTypeOrAnonymous anonymous : type.getTypeOrAnonymousList()) {
      args.add(getTypeFromTypeOrAnonymous(anonymous));
    }
    ResultHolder retval = args.get(args.size() - 1);
    args.remove(args.size() - 1);

    if (args.size() == 1 && args.get(0).getType().isVoid()) {
      args.remove(0);
    }

    return new SpecificFunctionReference(args, retval, null, type).createHolder();
  }

  @NotNull
  static public ResultHolder getTypeFromType(@NotNull LogtalkType type) {
    //System.out.println("Type:" + type);
    //System.out.println("Type:" + type.getText());
    LogtalkReferenceExpression expression = type.getReferenceExpression();
    LogtalkClassReference reference = new LogtalkClassReference(expression.getText(), expression);
    LogtalkTypeParam param = type.getTypeParam();
    ArrayList<ResultHolder> references = new ArrayList<ResultHolder>();
    if (param != null) {
      for (LogtalkTypeListPart part : param.getTypeList().getTypeListPartList()) {
        for (LogtalkFunctionType fnType : part.getFunctionTypeList()) {
          references.add(getTypeFromFunctionType(fnType));
        }
        for (LogtalkTypeOrAnonymous anonymous : part.getTypeOrAnonymousList()) {
          references.add(getTypeFromTypeOrAnonymous(anonymous));
        }
      }
    }
    //type.getTypeParam();
    return SpecificLogtalkClassReference.withGenerics(reference, references.toArray(ResultHolder.EMPTY)).createHolder();
  }

  @NotNull
  static public ResultHolder getTypeFromTypeOrAnonymous(@NotNull LogtalkTypeOrAnonymous typeOrAnonymous) {
    // @TODO: Do a proper type resolving
    LogtalkType type = typeOrAnonymous.getType();
    if (type != null) {
      return getTypeFromType(type);
    }
    return SpecificTypeReference.getDynamic(typeOrAnonymous).createHolder();
  }

  @NotNull
  static public ResultHolder getPsiElementType(PsiElement element) {
    return getPsiElementType(element, null).result;
  }

  static private void checkMethod(PsiElement element, LogtalkExpressionEvaluatorContext context) {
    //final ResultHolder retval = context.getReturnType();

    if (!(element instanceof LogtalkMethod)) return;
    final LogtalkTypeTag typeTag = UsefulPsiTreeUtil.getChild(element, LogtalkTypeTag.class);
    ResultHolder expectedType = SpecificTypeReference.getDynamic(element).createHolder();
    if (typeTag == null) {
      final List<ReturnInfo> infos = context.getReturnInfos();
      if (!infos.isEmpty()) {
        expectedType = infos.get(0).type;
      }
    } else {
      expectedType = getTypeFromTypeTag(typeTag, element);
    }

    if (expectedType == null) return;
    for (ReturnInfo retinfo : context.getReturnInfos()) {
      if (expectedType.canAssign(retinfo.type)) continue;
      context.addError(
        retinfo.element,
        "Can't return " + retinfo.type + ", expected " + expectedType.toStringWithoutConstant()
      );
    }
  }

  @NotNull
  static public LogtalkExpressionEvaluatorContext getPsiElementType(PsiElement element, @Nullable AnnotationHolder holder) {
    return evaluateFunction(new LogtalkExpressionEvaluatorContext(element, holder));
  }

  // @TODO: hack to avoid stack overflow, until a proper non-static fix is done
  //        At least, we've made it thread local, so the threads aren't stomping on each other any more.
  static private ThreadLocal<? extends Set<PsiElement>> processedElements = new ThreadLocal<HashSet<PsiElement>>() {
    @Override
    protected HashSet<PsiElement> initialValue() {
      return new HashSet<PsiElement>();
    }
  };

  @NotNull
  static public LogtalkExpressionEvaluatorContext evaluateFunction(@NotNull LogtalkExpressionEvaluatorContext context) {
    PsiElement element = context.root;
    if (processedElements.get().contains(element)) {
      context.result = SpecificLogtalkClassReference.primitive("Dynamic", element).createHolder();
      return context;
    }

    processedElements.get().add(element);
    try {
      LogtalkExpressionEvaluator.evaluate(element, context);
      checkMethod(element.getParent(), context);

      for (LogtalkExpressionEvaluatorContext lambda : context.lambdas) {
        evaluateFunction(lambda);
      }

      return context;
    }
    finally {
      processedElements.get().remove(element);
    }
  }

  static private SpecificLogtalkClassReference createPrimitiveType(String type, PsiElement element, Object constant) {
    return SpecificLogtalkClassReference.withoutGenerics(new LogtalkClassReference(type, element), constant);
  }
}
