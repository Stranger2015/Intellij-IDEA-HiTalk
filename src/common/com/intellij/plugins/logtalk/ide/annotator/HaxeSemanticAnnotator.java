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
package com.intellij.plugins.logtalk.ide.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.ide.quickfix.CreateGetterSetterQuickfix;
import com.intellij.plugins.haxe.lang.lexer.LogtalkTokenTypes;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.*;
import com.intellij.plugins.haxe.model.fixer.LogtalkFixer;
import com.intellij.plugins.haxe.model.fixer.LogtalkModifierAddFixer;
import com.intellij.plugins.haxe.model.fixer.LogtalkModifierRemoveFixer;
import com.intellij.plugins.haxe.model.fixer.LogtalkModifierReplaceVisibilityFixer;
import com.intellij.plugins.haxe.model.type.LogtalkTypeCompatible;
import com.intellij.plugins.haxe.model.type.LogtalkTypeResolver;
import com.intellij.plugins.haxe.model.type.ResultHolder;
import com.intellij.plugins.haxe.util.LogtalkAbstractEnumUtil;
import com.intellij.plugins.haxe.util.LogtalkResolveUtil;
import com.intellij.plugins.haxe.util.PsiFileUtils;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.plugins.logtalk.ide.quickfix.CreateGetterSetterQuickfix;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.intellij.util.containers.ContainerUtil.getFirstItem;

public class LogtalkSemanticAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    analyzeSingle(element, holder);
  }

  private static void analyzeSingle(final PsiElement element, AnnotationHolder holder) {
    if (element instanceof LogtalkPackageStatement) {
      PackageChecker.check((LogtalkPackageStatement)element, holder);
    } else if (element instanceof LogtalkMethod) {
      MethodChecker.check((LogtalkMethod)element, holder);
    } else if (element instanceof LogtalkClass) {
      ClassChecker.check((LogtalkClass)element, holder);
    } else if (element instanceof LogtalkType) {
      TypeChecker.check((LogtalkType)element, holder);
    } else if (element instanceof LogtalkVarDeclaration) {
      FieldChecker.check((LogtalkVarDeclaration)element, holder);
    } else if (element instanceof LogtalkLocalVarDeclaration) {
      LocalVarChecker.check((LogtalkLocalVarDeclaration)element, holder);
    } else if (element instanceof LogtalkStringLiteralExpression) {
      StringChecker.check((LogtalkStringLiteralExpression)element, holder);
    }
  }
}

class TypeTagChecker {
  public static void check(
    final PsiElement erroredElement,
    final LogtalkTypeTag tag,
    final LogtalkVarInit initExpression,
    boolean requireConstant,
    final AnnotationHolder holder
  ) {
    final ResultHolder type1 = LogtalkTypeResolver.getTypeFromTypeTag(tag, erroredElement);
    final ResultHolder type2 = getTypeFromVarInit(initExpression);

    final LogtalkDocumentModel document = LogtalkDocumentModel.fromElement(tag);
    if (!type1.canAssign(type2)) {
      // @TODO: Move to bundle
      Annotation annotation =
        holder.createErrorAnnotation(erroredElement, "Incompatible type " + type1 + " can't be assigned from " + type2);
      annotation.registerFix(new LogtalkFixer("Change type") {
        @Override
        public void run() {
          document.replaceElementText(tag, ":" + type2.toStringWithoutConstant());
        }
      });
      annotation.registerFix(new LogtalkFixer("Remove init") {
        @Override
        public void run() {
          document.replaceElementText(initExpression, "", StripSpaces.BEFORE);
        }
      });
    } else if (requireConstant && type2.getType().getConstant() == null) {
      // TODO: Move to bundle
      holder.createErrorAnnotation(erroredElement, "Parameter default type should be constant but was " + type2);
    }
  }

  @NotNull
  private static ResultHolder getTypeFromVarInit(LogtalkVarInit init) {
    final ResultHolder abstractEnumFieldInitType = LogtalkAbstractEnumUtil.getStaticMemberExpression(init.getExpression());
    if (abstractEnumFieldInitType != null) {
      return abstractEnumFieldInitType;
    }
    // fallback to simple init expression
    return LogtalkTypeResolver.getPsiElementType(init);
  }
}

class LocalVarChecker {
  public static void check(final LogtalkLocalVarDeclaration var, final AnnotationHolder holder) {
    LogtalkLocalVarModel local = new LogtalkLocalVarModel(var);
    if (local.hasInitializer() && local.hasTypeTag()) {
      TypeTagChecker.check(local.getBasePsi(), local.getTypeTagPsi(), local.getInitializerPsi(), false, holder);
    }
  }
}

class FieldChecker {
  public static void check(final LogtalkVarDeclaration var, final AnnotationHolder holder) {
    LogtalkFieldModel field = new LogtalkFieldModel(var);
    if (field.isProperty()) {
      checkProperty(field, holder);
    }
    if (field.hasInitializer() && field.hasTypeTag()) {
      TypeTagChecker.check(field.getBasePsi(), field.getTypeTagPsi(), field.getInitializerPsi(), false, holder);
    }

    // Checking for variable redefinition.
    HashSet<LogtalkClassModel> classSet = new HashSet<>();
    LogtalkClassModel fieldDeclaringClass = field.getDeclaringClass();
    classSet.add(fieldDeclaringClass);
    while (fieldDeclaringClass != null) {
      fieldDeclaringClass = fieldDeclaringClass.getParentClass();
      if (classSet.contains(fieldDeclaringClass)) {
        break;
      } else {
        classSet.add(fieldDeclaringClass);
      }
      if (fieldDeclaringClass != null) {
        for (LogtalkFieldModel parentField : fieldDeclaringClass.getFields()) {
          if (parentField.getName().equals(field.getName())) {
            String message;
            if (parentField.isStatic()) {
              message = LogtalkBundle.message("logtalk.semantic.static.field.override", field.getName());
              holder.createWeakWarningAnnotation(field.getNameOrBasePsi(), message);
            } else {
              message = LogtalkBundle.message("logtalk.semantic.variable.redefinition", field.getName(), fieldDeclaringClass.getName());
              holder.createErrorAnnotation(field.getBasePsi(), message);
            }
            break;
          }
        }
      }
    }
  }

  private static void checkProperty(final LogtalkFieldModel field, final AnnotationHolder holder) {
    final LogtalkDocumentModel document = field.getDocument();

    if (field.getGetterPsi() != null && !field.getGetterType().isValidGetter()) {
      holder.createErrorAnnotation(field.getGetterPsi(), "Invalid getter accessor");
    }

    if (field.getSetterPsi() != null && !field.getSetterType().isValidSetter()) {
      holder.createErrorAnnotation(field.getSetterPsi(), "Invalid setter accessor");
    }

    checkPropertyAccessorMethods(field, holder);

    if (field.isProperty() && !field.isRealVar() && field.hasInitializer()) {
      final LogtalkVarInit psi = field.getInitializerPsi();
      Annotation annotation = holder.createErrorAnnotation(
        field.getInitializerPsi(),
        "This field cannot be initialized because it is not a real variable"
      );
      annotation.registerFix(new LogtalkFixer("Remove init") {
        @Override
        public void run() {
          document.replaceElementText(psi, "", StripSpaces.BEFORE);
        }
      });
      annotation.registerFix(new LogtalkFixer("Add @:isVar") {
        @Override
        public void run() {
          field.getModifiers().addModifier(LogtalkModifierType.IS_VAR);
        }
      });
      if (field.getSetterPsi() != null) {
        annotation.registerFix(new LogtalkFixer("Make setter null") {
          @Override
          public void run() {
            document.replaceElementText(field.getSetterPsi(), "null");
          }
        });
      }
    }
  }

  private static void checkPropertyAccessorMethods(final LogtalkFieldModel field, final AnnotationHolder holder) {
    if (field.getDeclaringClass().isInterface()) {
      return;
    }

    if (field.getGetterType() == LogtalkAccessorType.GET) {
      final String methodName = "get_" + field.getName();

      LogtalkMethodModel method = field.getDeclaringClass().getMethod(methodName);
      if (method == null && field.getGetterPsi() != null) {
        holder
          .createErrorAnnotation(field.getGetterPsi(), "Can't find method " + methodName)
          .registerFix(new CreateGetterSetterQuickfix(field.getDeclaringClass(), field, true));
      }
    }

    if (field.getSetterType() == LogtalkAccessorType.SET) {
      final String methodName = "set_" + field.getName();

      LogtalkMethodModel method = field.getDeclaringClass().getMethod(methodName);
      if (method == null && field.getSetterPsi() != null) {
        holder
          .createErrorAnnotation(field.getSetterPsi(), "Can't find method " + methodName)
          .registerFix(new CreateGetterSetterQuickfix(field.getDeclaringClass(), field, false));
      }
    }
  }
}

class TypeChecker {
  static public void check(final LogtalkType type, final AnnotationHolder holder) {
    check(type.getReferenceExpression().getIdentifier(), holder);
  }

  static public void check(final PsiIdentifier identifier, final AnnotationHolder holder) {
    if (identifier == null) return;
    final String typeName = identifier.getText();
    if (!LogtalkClassModel.isValidClassName(typeName)) {
      Annotation annotation = holder.createErrorAnnotation(identifier, "Type name must start by upper case");
      annotation.registerFix(new LogtalkFixer("Change name") {
        @Override
        public void run() {
          LogtalkDocumentModel.fromElement(identifier).replaceElementText(
            identifier,
            typeName.substring(0, 1).toUpperCase() + typeName.substring(1)
          );
        }
      });
    }
  }
}

class ClassChecker {
  static public void check(final LogtalkClass clazzPsi, final AnnotationHolder holder) {
    LogtalkClassModel clazz = clazzPsi.getModel();
    checkDuplicatedFields(clazz, holder);
    checkClassName(clazz, holder);
    checkInterfaces(clazz, holder);
    checkExtends(clazz, holder);
    checkInterfacesMethods(clazz, holder);
  }

  static private void checkDuplicatedFields(final LogtalkClassModel clazz, final AnnotationHolder holder) {
    Map<String, LogtalkMemberModel> map = new HashMap<String, LogtalkMemberModel>();
    Set<LogtalkMemberModel> repeatedMembers = new HashSet<LogtalkMemberModel>();
    for (LogtalkMemberModel member : clazz.getMembersSelf()) {
      final String memberName = member.getName();
      LogtalkMemberModel repeatedMember = map.get(memberName);
      if (repeatedMember != null) {
        repeatedMembers.add(member);
        repeatedMembers.add(repeatedMember);
      } else {
        map.put(memberName, member);
      }
    }

    for (LogtalkMemberModel member : repeatedMembers) {
      holder.createErrorAnnotation(member.getNameOrBasePsi(), "Duplicate class field declaration : " + member.getName());
    }


    //Duplicate class field declaration
  }

  static private void checkClassName(final LogtalkClassModel clazz, final AnnotationHolder holder) {
    TypeChecker.check(clazz.getNamePsi(), holder);
  }

  private static void checkExtends(final LogtalkClassModel clazz, final AnnotationHolder holder) {
    LogtalkClassModel reference = clazz.getParentClass();
    if (reference != null) {
      if (isAnonymousType(clazz)) {
        if (!isAnonymousType(reference)) {
          // @TODO: Move to bundle
          holder.createErrorAnnotation(clazz.haxeClass.getLogtalkExtendsList().get(0), "Not an anonymous type");
        }
      } else if (clazz.isInterface()) {
        if (!reference.isInterface()) {
          // @TODO: Move to bundle
          holder.createErrorAnnotation(reference.getPsi(), "Not an interface");
        }
      } else if (!reference.isClass()) {
        // @TODO: Move to bundle
        holder.createErrorAnnotation(reference.getPsi(), "Not a class");
      }

      final String qname1 = reference.haxeClass.getQualifiedName();
      final String qname2 = clazz.haxeClass.getQualifiedName();
      if (qname1.equals(qname2)) {
        // @TODO: Move to bundle
        holder.createErrorAnnotation(clazz.haxeClass.getLogtalkExtendsList().get(0), "Cannot extend self");
      }
    }
  }

  static private boolean isAnonymousType(LogtalkClassModel clazz) {
    if (clazz != null && clazz.haxeClass != null) {
      LogtalkClass haxeClass = clazz.haxeClass;
      if (haxeClass instanceof LogtalkAnonymousType) {
        return true;
      }
      if (haxeClass instanceof LogtalkTypedefDeclaration) {
        LogtalkTypeOrAnonymous anonOrType = getFirstItem(((LogtalkTypedefDeclaration)haxeClass).getTypeOrAnonymousList());
        if (anonOrType != null) {
          return anonOrType.getAnonymousType() != null;
        }
      }
    }
    return false;
  }

  private static void checkInterfaces(final LogtalkClassModel clazz, final AnnotationHolder holder) {
    for (LogtalkClassReferenceModel interfaze : clazz.getImplementingInterfaces()) {
      if (interfaze.getLogtalkClass() == null || !interfaze.getLogtalkClass().isInterface()) {
        // @TODO: Move to bundle
        holder.createErrorAnnotation(interfaze.getPsi(), "Not an interface");
      }
    }
  }

  private static void checkInterfacesMethods(final LogtalkClassModel clazz, final AnnotationHolder holder) {
    for (LogtalkClassReferenceModel reference : clazz.getImplementingInterfaces()) {
      checkInterfaceMethods(clazz, reference, holder);
    }
  }

  private static void checkInterfaceMethods(
    final LogtalkClassModel clazz,
    final LogtalkClassReferenceModel intReference,
    final AnnotationHolder holder
  ) {
    final List<LogtalkMethodModel> missingMethods = new ArrayList<LogtalkMethodModel>();
    final List<String> missingMethodsNames = new ArrayList<String>();

    if (intReference.getLogtalkClass() != null) {
      for (LogtalkMethodModel intMethod : intReference.getLogtalkClass().getMethods()) {
        if (!intMethod.isStatic()) {
          // Implemented method not necessarily located in current class
          final PsiMethod[] methods = clazz.haxeClass.findMethodsByName(intMethod.getName(), true);
          final PsiMethod psiMethod = ContainerUtil.find(methods, new Condition<PsiMethod>() {
            @Override
            public boolean value(PsiMethod method) {
              return !(method instanceof LogtalkFunctionPrototypeDeclarationWithAttributes);
            }
          });

          if (psiMethod == null) {
            missingMethods.add(intMethod);
            missingMethodsNames.add(intMethod.getName());
          } else {
            final LogtalkMethod method = (LogtalkMethod)psiMethod;
            final LogtalkMethodModel methodModel = method.getModel();

            // We should check if signature in inherited method differs from method provided by interface
            if (methodModel.getDeclaringClass() != clazz) {
              if (MethodChecker.checkIfMethodSignatureDiffers(methodModel, intMethod)) {
                final LogtalkClass parentClass = methodModel.getDeclaringClass().haxeClass;

                final String errorMessage = LogtalkBundle.message(
                  "logtalk.semantic.implemented.super.method.signature.differs",
                  method.getName(),
                  parentClass.getQualifiedName(),
                  intMethod.getPresentableText(LogtalkMethodContext.NO_EXTENSION),
                  methodModel.getPresentableText(LogtalkMethodContext.NO_EXTENSION)
                );

                holder.createErrorAnnotation(intReference.getPsi(), errorMessage);
              }
            } else {
              MethodChecker.checkMethodsSignatureCompatibility(methodModel, intMethod, holder);
            }
          }
        }
      }
    }

    if (missingMethods.size() > 0) {
      // @TODO: Move to bundle
      Annotation annotation = holder.createErrorAnnotation(
        intReference.getPsi(),
        "Not implemented methods: " + StringUtils.join(missingMethodsNames, ", ")
      );
      annotation.registerFix(new LogtalkFixer("Implement methods") {
        @Override
        public void run() {
          clazz.addMethodsFromPrototype(missingMethods);
        }
      });
    }
  }
}

class MethodChecker {
  static public void check(final LogtalkMethod methodPsi, final AnnotationHolder holder) {
    final LogtalkMethodModel currentMethod = methodPsi.getModel();
    checkTypeTagInInterfacesAndExternClass(currentMethod, holder);
    checkMethodArguments(currentMethod, holder);
    checkOverride(methodPsi, holder);
    if (LogtalkSemanticAnnotatorConfig.ENABLE_EXPERIMENTAL_BODY_CHECK) {
      MethodBodyChecker.check(methodPsi, holder);
    }
    //currentMethod.getBodyPsi()
  }

  private static void checkTypeTagInInterfacesAndExternClass(final LogtalkMethodModel currentMethod, final AnnotationHolder holder) {
    LogtalkClassModel currentClass = currentMethod.getDeclaringClass();
    if (currentClass.isExtern() || currentClass.isInterface()) {
      if (currentMethod.getReturnTypeTagPsi() == null && !currentMethod.isConstructor()) {
        holder.createErrorAnnotation(currentMethod.getNameOrBasePsi(), LogtalkBundle.message("logtalk.semantic.type.required"));
      }
      for (final LogtalkParameterModel param : currentMethod.getParameters()) {
        if (param.getTypeTagPsi() == null) {
          holder.createErrorAnnotation(param.getNameOrBasePsi(), LogtalkBundle.message("logtalk.semantic.type.required"));
        }
      }
    }
  }

  private static void checkMethodArguments(final LogtalkMethodModel currentMethod, final AnnotationHolder holder) {
    boolean hasOptional = false;
    HashMap<String, PsiElement> argumentNames = new HashMap<String, PsiElement>();
    for (final LogtalkParameterModel param : currentMethod.getParameters()) {
      String paramName = param.getName();

      if (param.hasOptionalPsi() && param.getVarInitPsi() != null) {
        // @TODO: Move to bundle
        holder.createWarningAnnotation(param.getOptionalPsi(), "Optional not needed when specified an init value");
      }
      if (param.getVarInitPsi() != null && param.getTypeTagPsi() != null) {
        TypeTagChecker.check(
          param.getBasePsi(),
          param.getTypeTagPsi(),
          param.getVarInitPsi(),
          true,
          holder
        );
      }
      if (param.isOptional()) {
        hasOptional = true;
      } else if (hasOptional) {
        // @TODO: Move to bundle
        holder.createWarningAnnotation(param.getBasePsi(), "Non-optional argument after optional argument");
      }

      if (argumentNames.containsKey(paramName)) {
        // @TODO: Move to bundle
        holder.createWarningAnnotation(param.getNameOrBasePsi(), "Repeated argument name '" + paramName + "'");
        holder.createWarningAnnotation(argumentNames.get(paramName), "Repeated argument name '" + paramName + "'");
      } else {
        argumentNames.put(paramName, param.getNameOrBasePsi());
      }
    }
  }

  private static void checkOverride(final LogtalkMethod methodPsi, final AnnotationHolder holder) {
    final LogtalkMethodModel currentMethod = methodPsi.getModel();
    final LogtalkClassModel currentClass = currentMethod.getDeclaringClass();
    final LogtalkModifiersModel currentModifiers = currentMethod.getModifiers();

    final LogtalkClassModel parentClass = (currentClass != null) ? currentClass.getParentClass() : null;
    final LogtalkMethodModel parentMethod =
      ((parentClass != null) && parentClass != null) ? parentClass.getMethod(currentMethod.getName()) : null;
    final LogtalkModifiersModel parentModifiers = (parentMethod != null) ? parentMethod.getModifiers() : null;

    boolean requiredOverride = false;

    if (currentMethod.isConstructor()) {
      requiredOverride = false;
      if (currentModifiers.hasModifier(LogtalkModifierType.STATIC)) {
        // @TODO: Move to bundle
        holder.createErrorAnnotation(currentMethod.getNameOrBasePsi(), "Constructor can't be static").registerFix(
          new LogtalkModifierRemoveFixer(currentModifiers, LogtalkModifierType.STATIC)
        );
      }
    } else if (currentMethod.isStaticInit()) {
      requiredOverride = false;
      if (!currentModifiers.hasModifier(LogtalkModifierType.STATIC)) {
        holder.createErrorAnnotation(currentMethod.getNameOrBasePsi(), "__init__ must be static").registerFix(
          new LogtalkModifierAddFixer(currentModifiers, LogtalkModifierType.STATIC)
        );
      }
    } else if (parentMethod != null) {
      if (parentMethod.isStatic()) {
        holder.createWarningAnnotation(currentMethod.getNameOrBasePsi(), "Method '" + currentMethod.getName()
                                                                         + "' overrides a static method of a superclass");
      } else {
        requiredOverride = true;

        if (parentModifiers.hasAnyModifier(LogtalkModifierType.INLINE, LogtalkModifierType.STATIC, LogtalkModifierType.FINAL)) {
          Annotation annotation =
            holder.createErrorAnnotation(currentMethod.getNameOrBasePsi(), "Can't override static, inline or final methods");
          for (LogtalkModifierType mod : new LogtalkModifierType[]{LogtalkModifierType.FINAL, LogtalkModifierType.INLINE, LogtalkModifierType.STATIC}) {
            if (parentModifiers.hasModifier(mod)) {
              annotation.registerFix(
                new LogtalkModifierRemoveFixer(parentModifiers, mod, "Remove " + mod.s + " from " + parentMethod.getFullName())
              );
            }
          }
        }

        if (currentModifiers.getVisibility().hasLowerVisibilityThan(parentModifiers.getVisibility())) {
          Annotation annotation = holder.createErrorAnnotation(
            currentMethod.getNameOrBasePsi(),
            "Field " +
            currentMethod.getName() +
            " has less visibility (public/private) than superclass one"
          );
          annotation.registerFix(
            new LogtalkModifierReplaceVisibilityFixer(currentModifiers, parentModifiers.getVisibility(), "Change current method visibility"));
          annotation.registerFix(
            new LogtalkModifierReplaceVisibilityFixer(parentModifiers, currentModifiers.getVisibility(), "Change parent method visibility"));
        }
      }
    }

    //System.out.println(aClass);
    if (currentModifiers.hasModifier(LogtalkModifierType.OVERRIDE) && !requiredOverride) {
      holder.createErrorAnnotation(currentModifiers.getModifierPsi(LogtalkModifierType.OVERRIDE), "Overriding nothing").registerFix(
        new LogtalkModifierRemoveFixer(currentModifiers, LogtalkModifierType.OVERRIDE)
      );
    } else if (requiredOverride) {
      if (!currentModifiers.hasModifier(LogtalkModifierType.OVERRIDE)) {
        holder.createErrorAnnotation(currentMethod.getNameOrBasePsi(), "Must override").registerFix(
          new LogtalkModifierAddFixer(currentModifiers, LogtalkModifierType.OVERRIDE)
        );
      } else {
        // It is rightly overriden. Now check the signature.
        checkMethodsSignatureCompatibility(currentMethod, parentMethod, holder);
      }
    }
  }

  static void checkMethodsSignatureCompatibility(
    @NotNull final LogtalkMethodModel currentMethod,
    @NotNull final LogtalkMethodModel parentMethod,
    final AnnotationHolder holder
  ) {
    final LogtalkDocumentModel document = currentMethod.getDocument();

    List<LogtalkParameterModel> currentParameters = currentMethod.getParameters();
    final List<LogtalkParameterModel> parentParameters = parentMethod.getParameters();
    int minParameters = Math.min(currentParameters.size(), parentParameters.size());

    if (currentParameters.size() > parentParameters.size()) {
      for (int n = minParameters; n < currentParameters.size(); n++) {
        final LogtalkParameterModel currentParam = currentParameters.get(n);
        holder.createErrorAnnotation(currentParam.getBasePsi(), "Unexpected argument").registerFix(
          new LogtalkFixer("Remove argument") {
            @Override
            public void run() {
              currentParam.remove();
            }
          });
      }
    } else if (currentParameters.size() != parentParameters.size()) {
      holder.createErrorAnnotation(
        currentMethod.getNameOrBasePsi(),
        "Not matching arity expected " +
        parentParameters.size() +
        " arguments but found " +
        currentParameters.size()
      );
    }

    for (int n = 0; n < minParameters; n++) {
      final LogtalkParameterModel currentParam = currentParameters.get(n);
      final LogtalkParameterModel parentParam = parentParameters.get(n);
      if (!LogtalkTypeCompatible.canAssignToFrom(currentParam.getType(), parentParam.getType())) {
        holder.createErrorAnnotation(
          currentParam.getBasePsi(),
          "Type " + currentParam.getType() + " is not compatible with " + parentParam.getType()).registerFix
          (
            new LogtalkFixer("Change type") {
              @Override
              public void run() {
                document.replaceElementText(currentParam.getTypeTagPsi(), parentParam.getTypeTagPsi().getText());
              }
            }
          );
      }

      if (currentParam.hasOptionalPsi() != parentParam.hasOptionalPsi()) {
        final boolean removeOptional = currentParam.hasOptionalPsi();

        String errorMessage;
        if (parentMethod.getDeclaringClass().isInterface()) {
          errorMessage = removeOptional ? "logtalk.semantic.implemented.method.parameter.required"
                                        : "logtalk.semantic.implemented.method.parameter.optional";
        } else {
          errorMessage = removeOptional ? "logtalk.semantic.overwritten.method.parameter.required"
                                        : "logtalk.semantic.overwritten.method.parameter.optional";
        }

        errorMessage = LogtalkBundle.message(errorMessage, parentParam.getPresentableText(),
                                          parentMethod.getDeclaringClass().getName() + "." + parentMethod.getName());

        final Annotation annotation = holder.createErrorAnnotation(currentParam.getBasePsi(), errorMessage);
        final String localFixName = LogtalkBundle.message(removeOptional ? "logtalk.semantic.method.parameter.optional.remove"
                                                                      : "logtalk.semantic.method.parameter.optional.add");

        annotation.registerFix(
          new LogtalkFixer(localFixName) {
            @Override
            public void run() {
              if (removeOptional) {
                currentParam.getOptionalPsi().delete();
              } else {
                PsiElement element = currentParam.getBasePsi();
                document.addTextBeforeElement(element.getFirstChild(), "?");
              }
            }
          }
        );
      }
    }

    ResultHolder currentResult = currentMethod.getResultType();
    ResultHolder parentResult = parentMethod.getResultType();
    if (!currentResult.canAssign(parentResult)) {
      PsiElement psi = currentMethod.getReturnTypeTagOrNameOrBasePsi();
      holder.createErrorAnnotation(psi, "Not compatible return type " + currentResult + " != " + parentResult);
    }
  }

  // Fast check without annotations
  static boolean checkIfMethodSignatureDiffers(LogtalkMethodModel source, LogtalkMethodModel prototype) {
    final List<LogtalkParameterModel> sourceParameters = source.getParameters();
    final List<LogtalkParameterModel> prototypeParameters = prototype.getParameters();

    if (sourceParameters.size() != prototypeParameters.size()) {
      return true;
    }

    final int parametersCount = sourceParameters.size();

    for (int n = 0; n < parametersCount; n++) {
      final LogtalkParameterModel sourceParam = sourceParameters.get(n);
      final LogtalkParameterModel prototypeParam = prototypeParameters.get(n);
      if (!LogtalkTypeCompatible.canAssignToFrom(sourceParam.getType(), prototypeParam.getType()) ||
          sourceParam.isOptional() != prototypeParam.isOptional()) {
        return true;
      }
    }

    ResultHolder currentResult = source.getResultType();
    ResultHolder prototypeResult = prototype.getResultType();

    return !currentResult.canAssign(prototypeResult);
  }
}

class PackageChecker {
  static public void check(final LogtalkPackageStatement element, final AnnotationHolder holder) {
    final LogtalkReferenceExpression expression = element.getReferenceExpression();
    String packageName = (expression != null) ? expression.getText() : "";
    PsiDirectory fileDirectory = element.getContainingFile().getParent();
    if (fileDirectory == null) return;
    List<PsiFileSystemItem> fileRange = PsiFileUtils.getRange(PsiFileUtils.findRoot(fileDirectory), fileDirectory);
    fileRange.remove(0);
    String actualPath = PsiFileUtils.getListPath(fileRange);
    final String actualPackage = actualPath.replace('/', '.');
    final String actualPackage2 = LogtalkResolveUtil.getPackageName(element.getContainingFile());
    // @TODO: Should use LogtalkResolveUtil

    for (String s : StringUtils.split(packageName, '.')) {
      if (!s.substring(0, 1).toLowerCase().equals(s.substring(0, 1))) {
        //LogtalkSemanticError.addError(element, new LogtalkSemanticError("Package name '" + s + "' must start with a lower case character"));
        // @TODO: Move to bundle
        holder.createErrorAnnotation(element, "Package name '" + s + "' must start with a lower case character");
      }
    }

    if (!packageName.equals(actualPackage)) {
      holder.createErrorAnnotation(
        element,
        "Invalid package name! '" + packageName + "' should be '" + actualPackage + "'").registerFix(
        new LogtalkFixer("Fix package") {
          @Override
          public void run() {
            Document document =
              PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());

            if (expression != null) {
              TextRange range = expression.getTextRange();
              document.replaceString(range.getStartOffset(), range.getEndOffset(), actualPackage);
            } else {
              int offset =
                element.getNode().findChildByType(LogtalkTokenTypes.OSEMI).getTextRange().getStartOffset();
              document.replaceString(offset, offset, actualPackage);
            }
          }
        }
      );
    }
  }
}

class MethodBodyChecker {
  public static void check(LogtalkMethod psi, AnnotationHolder holder) {
    final LogtalkMethodModel method = psi.getModel();
    LogtalkTypeResolver.getPsiElementType(method.getBodyPsi(), holder);
  }
}

class StringChecker {
  public static void check(LogtalkStringLiteralExpression psi, AnnotationHolder holder) {
    if (isSingleQuotesRequired(psi)) {
      holder.createWarningAnnotation(psi, "Expressions that contains string interpolation should be wrapped with single quotes");
    }
  }

  private static boolean isSingleQuotesRequired(LogtalkStringLiteralExpression psi) {
    return (psi.getLongTemplateEntryList().size() > 0 || psi.getShortTemplateEntryList().size() > 0) &&
           psi.getFirstChild().textContains('"');
  }
}