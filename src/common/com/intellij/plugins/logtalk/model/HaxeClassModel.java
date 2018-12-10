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

import com.intellij.plugins.haxe.LogtalkComponentType;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.model.type.*;
import com.intellij.plugins.haxe.util.UsefulPsiTreeUtil;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.plugins.logtalk.lang.psi.*;
import com.intellij.plugins.logtalk.util.UsefulPsiTreeUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMember;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class LogtalkClassModel implements LogtalkExposableModel {
  public final LogtalkClass haxeClass;

  public LogtalkClassModel(@NotNull LogtalkClass haxeClass) {
    this.haxeClass = haxeClass;
  }

  public LogtalkClassModel getParentClass() {
    List<LogtalkType> list = haxeClass.getLogtalkExtendsList();
    if (!list.isEmpty()) {
      PsiElement haxeClass = list.get(0).getReferenceExpression().resolve();
      if (haxeClass != null && haxeClass instanceof LogtalkClass) {
        return ((LogtalkClass)haxeClass).getModel();
      }
    }
    return null;
  }

  static public boolean isValidClassName(String name) {
    return name.substring(0, 1).equals(name.substring(0, 1).toUpperCase());
  }

  public LogtalkClassReference getReference() {
    return new LogtalkClassReference(this, this.getPsi());
  }

  @NotNull
  public ResultHolder getInstanceType() {
    return SpecificLogtalkClassReference.withoutGenerics(getReference()).createHolder();
  }

  public List<LogtalkClassReferenceModel> getInterfaceExtendingInterfaces() {
    List<LogtalkType> list = haxeClass.getLogtalkExtendsList();
    List<LogtalkClassReferenceModel> out = new ArrayList<LogtalkClassReferenceModel>();
    for (LogtalkType type : list) {
      out.add(new LogtalkClassReferenceModel(type));
    }
    return out;
  }

  public List<LogtalkClassReferenceModel> getImplementingInterfaces() {
    List<LogtalkType> list = haxeClass.getLogtalkImplementsList();
    List<LogtalkClassReferenceModel> out = new ArrayList<LogtalkClassReferenceModel>();
    for (LogtalkType type : list) {
      out.add(new LogtalkClassReferenceModel(type));
    }
    return out;
  }

  public boolean isExtern() {
    return haxeClass.isExtern();
  }

  public boolean isClass() {
    return !this.isAbstract() && (LogtalkComponentType.typeOf(haxeClass) == LogtalkComponentType.CLASS);
  }

  public boolean isInterface() {
    return LogtalkComponentType.typeOf(haxeClass) == LogtalkComponentType.INTERFACE;
  }

  public boolean isEnum() {
    return haxeClass.isEnum();
  }

  public boolean isTypedef() {
    return LogtalkComponentType.typeOf(haxeClass) == LogtalkComponentType.TYPEDEF;
  }

  public boolean isAbstract() {
    return haxeClass instanceof LogtalkAbstractClassDeclaration;
  }

  public boolean hasMeta(@NotNull String name) {
    return haxeClass.hasMeta(name);
  }

  @Nullable
  public LogtalkMacroClass getMeta(@NotNull String name) {
    return haxeClass.getMeta(name);
  }

  @Nullable
  public LogtalkTypeOrAnonymous getUnderlyingType() {
    if (!isAbstract()) return null;
    LogtalkAbstractClassDeclaration abstractDeclaration = (LogtalkAbstractClassDeclaration)haxeClass;
    LogtalkUnderlyingType underlyingType = abstractDeclaration.getUnderlyingType();
    if (underlyingType != null) {
      List<LogtalkTypeOrAnonymous> list = underlyingType.getTypeOrAnonymousList();
      if (!list.isEmpty()) {
        return list.get(0);
      }

      // TODO: What about function types?
    }
    return null;
  }

  // @TODO: this should be properly parsed in logtalk.bnf so searching for to is not required
  public List<LogtalkType> getAbstractToList() {
    if (!isAbstract()) return Collections.emptyList();
    List<LogtalkType> types = new LinkedList<LogtalkType>();
    for (LogtalkIdentifier id : UsefulPsiTreeUtil.getChildren(haxeClass, LogtalkIdentifier.class)) {
      if (id.getText().equals("to")) {
        PsiElement sibling = UsefulPsiTreeUtil.getNextSiblingNoSpaces(id);
        if (sibling instanceof LogtalkType) {
          types.add((LogtalkType)sibling);
        }
      }
    }
    return types;
  }

  // @TODO: this should be properly parsed in logtalk.bnf so searching for from is not required
  public List<LogtalkType> getAbstractFromList() {
    if (!isAbstract()) return Collections.emptyList();
    List<LogtalkType> types = new LinkedList<LogtalkType>();
    for (LogtalkIdentifier id : UsefulPsiTreeUtil.getChildren(haxeClass, LogtalkIdentifier.class)) {
      if (id.getText().equals("from")) {
        PsiElement sibling = UsefulPsiTreeUtil.getNextSiblingNoSpaces(id);
        if (sibling instanceof LogtalkType) {
          types.add((LogtalkType)sibling);
        }
      }
    }
    return types;
  }

  public boolean hasMethod(String name) {
    return getMethod(name) != null;
  }

  public boolean hasMethodSelf(String name) {
    LogtalkMethodModel method = getMethod(name);
    if (method == null) return false;
    return (method.getDeclaringClass() == this);
  }

  public LogtalkMethodModel getMethodSelf(String name) {
    LogtalkMethodModel method = getMethod(name);
    if (method == null) return null;
    return (method.getDeclaringClass() == this) ? method : null;
  }

  public LogtalkMethodModel getConstructorSelf() {
    return getMethodSelf("new");
  }

  public LogtalkMethodModel getConstructor() {
    return getMethod("new");
  }

  public boolean hasConstructor() {
    return getConstructor() != null;
  }

  public LogtalkMethodModel getParentConstructor() {
    LogtalkClassModel parentClass = getParentClass();
    while (parentClass != null) {
      LogtalkMethodModel constructorMethod = parentClass.getConstructor();
      if (constructorMethod != null) {
        return constructorMethod;
      }
      parentClass = parentClass.getParentClass();
    }
    return null;
  }

  public LogtalkMemberModel getMember(String name) {
    if (name == null) return null;
    final LogtalkMethodModel method = getMethod(name);
    final LogtalkFieldModel field = getField(name);
    return (method != null) ? method : field;
  }

  public List<LogtalkMemberModel> getMembers() {
    final List<LogtalkMemberModel> members = new ArrayList<>();
    members.addAll(getMethods());
    members.addAll(getFields());
    return members;
  }

  @NotNull
  public List<LogtalkMemberModel> getMembersSelf() {
    final List<LogtalkMemberModel> members = new ArrayList<>();
    LogtalkPsiCompositeElement body = getBodyPsi();
    if (body != null) {
      for (PsiElement element : body.getChildren()) {
        if (element instanceof LogtalkMethod || element instanceof LogtalkVarDeclaration) {
          LogtalkMemberModel model = LogtalkMemberModel.fromPsi(element);
          if (model != null) {
            members.add(model);
          }
        }
      }
    }
    return members;
  }

  public LogtalkFieldModel getField(String name) {
    LogtalkPsiField field = (LogtalkPsiField)haxeClass.findLogtalkFieldByName(name);
    if (field instanceof LogtalkVarDeclaration || field instanceof LogtalkAnonymousTypeField || field instanceof LogtalkEnumValueDeclaration) {
      return new LogtalkFieldModel(field);
    }
    return null;
  }

  public LogtalkMethodModel getMethod(String name) {
    LogtalkMethodPsiMixin method = (LogtalkMethodPsiMixin)haxeClass.findLogtalkMethodByName(name);
    return method != null ? method.getModel() : null;
  }

  public List<LogtalkMethodModel> getMethods() {
    List<LogtalkMethodModel> models = new ArrayList<LogtalkMethodModel>();
    for (LogtalkMethod method : haxeClass.getLogtalkMethods()) {
      models.add(method.getModel());
    }
    return models;
  }

  public List<LogtalkMethodModel> getMethodsSelf() {
    List<LogtalkMethodModel> models = new ArrayList<LogtalkMethodModel>();
    for (LogtalkMethod method : haxeClass.getLogtalkMethods()) {
      if (method.getContainingClass() == this.haxeClass) models.add(method.getModel());
    }
    return models;
  }

  public List<LogtalkMethodModel> getAncestorMethods() {
    List<LogtalkMethodModel> models = new ArrayList<LogtalkMethodModel>();
    for (LogtalkMethod method : haxeClass.getLogtalkMethods()) {
      if (method.getContainingClass() != this.haxeClass) models.add(method.getModel());
    }
    return models;
  }

  @NotNull
  public LogtalkClass getPsi() {
    return haxeClass;
  }

  @Nullable
  public LogtalkPsiCompositeElement getBodyPsi() {
    return (haxeClass instanceof LogtalkClassDeclaration) ? ((LogtalkClassDeclaration)haxeClass).getClassBody() : null;
  }

  @Nullable
  public PsiIdentifier getNamePsi() {
    return haxeClass.getNameIdentifier();
  }

  @NotNull
  public LogtalkDocumentModel getDocument() {
    return new LogtalkDocumentModel(haxeClass);
  }

  public String getName() {
    return haxeClass.getName();
  }

  @Override
  public PsiElement getBasePsi() {
    return this.haxeClass;
  }

  @Nullable
  @Override
  public LogtalkExposableModel getExhibitor() {
    return LogtalkFileModel.fromElement(haxeClass.getContainingFile());
  }

  @Nullable
  @Override
  public FullyQualifiedInfo getQualifiedInfo() {
    LogtalkExposableModel exhibitor = getExhibitor();
    if (exhibitor != null) {
      FullyQualifiedInfo containerInfo = exhibitor.getQualifiedInfo();
      if (containerInfo != null) {
        return new FullyQualifiedInfo(containerInfo.packagePath, containerInfo.fileName, getName(), null);
      }
    }

    return null;
  }

  public void addMethodsFromPrototype(List<LogtalkMethodModel> methods) {
    throw new NotImplementedException("Not implemented LogtalkClassMethod.addMethodsFromPrototype() : check LogtalkImplementMethodHandler");
  }

  public List<LogtalkFieldModel> getFields() {
    LogtalkPsiCompositeElement body = PsiTreeUtil.getChildOfAnyType(haxeClass, isEnum() ? LogtalkEnumBody.class : LogtalkClassBody.class);

    if (body != null) {
      return PsiTreeUtil.getChildrenOfAnyType(body, LogtalkVarDeclaration.class, LogtalkAnonymousTypeField.class, LogtalkEnumValueDeclaration.class)
        .stream()
        .map(LogtalkFieldModel::new)
        .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  public Set<LogtalkClassModel> getCompatibleTypes() {
    final Set<LogtalkClassModel> output = new LinkedHashSet<LogtalkClassModel>();
    writeCompatibleTypes(output);
    return output;
  }

  public void writeCompatibleTypes(Set<LogtalkClassModel> output) {
    // Own
    output.add(this);

    final LogtalkClassModel parentClass = this.getParentClass();

    // Parent classes
    if (parentClass != null) {
      if (!output.contains(parentClass)) {
        parentClass.writeCompatibleTypes(output);
      }
    }

    // Interfaces
    for (LogtalkClassReferenceModel model : this.getImplementingInterfaces()) {
      if (model == null) continue;
      final LogtalkClassModel aInterface = model.getLogtalkClass();
      if (aInterface == null) continue;
      if (!output.contains(aInterface)) {
        aInterface.writeCompatibleTypes(output);
      }
    }

    // @CHECK abstract FROM
    for (LogtalkType type : getAbstractFromList()) {
      final ResultHolder aTypeRef = LogtalkTypeResolver.getTypeFromType(type);
      SpecificLogtalkClassReference classType = aTypeRef.getClassType();
      if (classType != null) {
        classType.getLogtalkClassModel().writeCompatibleTypes(output);
      }
    }

    // @CHECK abstract TO
    for (LogtalkType type : getAbstractToList()) {
      final ResultHolder aTypeRef = LogtalkTypeResolver.getTypeFromType(type);
      SpecificLogtalkClassReference classType = aTypeRef.getClassType();
      if (classType != null) {
        classType.getLogtalkClassModel().writeCompatibleTypes(output);
      }
    }
  }

  public List<LogtalkGenericParamModel> getGenericParams() {
    final List<LogtalkGenericParamModel> out = new ArrayList<>();
    if (getPsi().getGenericParam() != null) {
      int index = 0;
      for (LogtalkGenericListPart part : getPsi().getGenericParam().getGenericListPartList()) {
        out.add(new LogtalkGenericParamModel(part, index));
        index++;
      }
    }
    return out;
  }

  public void addField(String name, SpecificTypeReference type) {
    this.getDocument().addTextAfterElement(getBodyPsi(), "\npublic var " + name + ":" + type.toStringWithoutConstant() + ";\n");
  }

  public void addMethod(String name) {
    this.getDocument().addTextAfterElement(getBodyPsi(), "\npublic function " + name + "() {\n}\n");
  }

  @Override
  public List<LogtalkModel> getExposedMembers() {
    // TODO ClassModel concept should be reviewed. We need to separate logic of abstracts, regular classes, enums, etc. Right now this class a bunch of if-else conditions. It looks dirty.
    ArrayList<LogtalkModel> out = new ArrayList<>();
    if (isClass()) {
      LogtalkClassBody body = UsefulPsiTreeUtil.getChild(haxeClass, LogtalkClassBody.class);
      if (body != null) {
        for (LogtalkNamedComponent declaration : PsiTreeUtil.getChildrenOfAnyType(body, LogtalkVarDeclaration.class, LogtalkMethod.class)) {
          if (!(declaration instanceof PsiMember)) continue;
          if (declaration instanceof LogtalkVarDeclaration) {
            LogtalkVarDeclaration varDeclaration = (LogtalkVarDeclaration)declaration;
            if (varDeclaration.isPublic() && varDeclaration.isStatic()) {
              out.add(new LogtalkFieldModel((LogtalkVarDeclaration)declaration));
            }
          } else {
            LogtalkMethod method = (LogtalkMethod)declaration;
            if (method.isStatic() && method.isPublic()) {
              out.add(new LogtalkMethodModel(method));
            }
          }
        }
      }
    } else if (isEnum()) {
      LogtalkEnumBody body = UsefulPsiTreeUtil.getChild(haxeClass, LogtalkEnumBody.class);
      if (body != null) {
        List<LogtalkEnumValueDeclaration> declarations = body.getEnumValueDeclarationList();
        for (LogtalkEnumValueDeclaration declaration : declarations) {
          out.add(new LogtalkFieldModel(declaration));
        }
      }
    }
    return out;
  }

  public static LogtalkClassModel fromElement(PsiElement element) {
    LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(element, LogtalkClass.class);
    if (haxeClass != null) {
      return new LogtalkClassModel(haxeClass);
    }

    return null;
  }

  public boolean isPublic() {
    return haxeClass.isPublic();
  }
}
