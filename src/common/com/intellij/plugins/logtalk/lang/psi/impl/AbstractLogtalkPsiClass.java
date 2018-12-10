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


import org.jetbrains.annotations.NotNull;

/**
 * @author: Fedor.Korotkov
 */
public abstract class AbstractLogtalkPsiClass extends AbstractLogtalkNamedComponent implements LogtalkClass {

  private static final Logger LOG = Logger.getInstance("#com.intellij.plugins.logtalk.lang.psi.impl.AbstractLogtalkPsiClass");

  static {
    LOG.info("Loaded AbstractLogtalkPsiClass");
    LOG.setLevel(Level.DEBUG);
  }

  public AbstractLogtalkPsiClass(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public LogtalkNamedComponent getTypeComponent() {
    return this;
  }

  @NotNull
  @Override
  public String getQualifiedName() {
    String name = getName();
    if (getParent() == null) {
      return name == null ? "" : name;
    }

    if (name == null && this instanceof LogtalkAnonymousType) {
      // restore name from parent
      final PsiElement typedefDecl = getParent().getParent();
      if (typedefDecl != null && typedefDecl instanceof LogtalkTypedefDeclaration) {
        name = ((LogtalkTypedefDeclaration)typedefDecl).getName();
      }
    }

    final String fileName = FileUtil.getNameWithoutExtension(getContainingFile().getName());
    String packageName = LogtalkResolveUtil.getPackageName(getContainingFile());

    if (name != null && isAncillaryClass(packageName, name, fileName)) {
      packageName = LogtalkResolveUtil.joinQName(packageName, fileName);
    }

    return LogtalkResolveUtil.joinQName(packageName, name);
  }

  private LogtalkClassModel _model = null;

  public LogtalkClassModel getModel() {
    if (_model == null) {
      if (this instanceof LogtalkEnumDeclaration) {
        _model = new LogtalkEnumModelImpl((LogtalkEnumDeclaration)this);
      } else if (this instanceof LogtalkExternClassDeclaration) {
        _model = new LogtalkExternClassModel((LogtalkExternClassDeclaration)this);
      } else if (this instanceof LogtalkAbstractClassDeclaration) {
        LogtalkAbstractClassDeclaration abstractDeclaration = (LogtalkAbstractClassDeclaration)this;
        if (abstractDeclaration.isEnum()) {
          _model = new LogtalkAbstractEnumModel(abstractDeclaration);
        } else {
          _model = new LogtalkAbstractClassModel(abstractDeclaration);
        }
      } else {
        _model = new LogtalkClassModel(this);
      }
    }

    return _model;
  }

  // check if class is declared inside logtalk module `MyClass.MySupportType`
  private boolean isAncillaryClass(@NotNull String packageName, @NotNull String name, @NotNull String fileName) {
    // if file name matches type name
    if (fileName.equals(name)) {
      return false;
    }
    // if StdTypes
    if (packageName.isEmpty() && fileName.equals("StdTypes")) {
      return false;
    }
    // file contains valid type declaration
    return LogtalkResolveUtil.findComponentDeclaration(getContainingFile(), name) != null;
  }

  @Override
  public boolean isExtern() {
    return (this instanceof LogtalkExternClassDeclaration || this instanceof LogtalkExternInterfaceDeclaration);
  }

  @Override
  public boolean isAbstract() {
    return (this instanceof LogtalkAbstractClassDeclaration);
  }

  @Override
  public boolean isInterface() {
    return LogtalkComponentType.typeOf(this) == LogtalkComponentType.INTERFACE;
  }

  @NotNull
  @Override
  public List<LogtalkType> getLogtalkExtendsList() {
    return LogtalkResolveUtil.findExtendsList(PsiTreeUtil.getChildOfType(this, LogtalkInheritList.class));
  }

  @NotNull
  @Override
  public List<LogtalkType> getLogtalkImplementsList() {
    return LogtalkResolveUtil.getImplementsList(PsiTreeUtil.getChildOfType(this, LogtalkInheritList.class));
  }

  @Override
  public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
    return super.add(element);
  }

  @NotNull
  @Override
  public List<LogtalkMethod> getLogtalkMethods() {
    // XXX: This implementation is equivalent to getAllMethods().  That
    //      may not be what we want.
    final List<LogtalkNamedComponent> alltypes = LogtalkResolveUtil.findNamedSubComponents(this);
    final List<LogtalkNamedComponent> methods = LogtalkResolveUtil.filterNamedComponentsByType(alltypes, LogtalkComponentType.METHOD);
    final List<LogtalkMethod> result = new ArrayList<>();
    for (LogtalkNamedComponent method : methods) {
      result.add((LogtalkMethod)method);
    }
    return result;
  }

  @NotNull
  @Override
  public List<LogtalkNamedComponent> getLogtalkFields() {
    final List<LogtalkNamedComponent> result = LogtalkResolveUtil.findNamedSubComponents(this);
    return LogtalkResolveUtil.filterNamedComponentsByType(result, LogtalkComponentType.FIELD);
  }

  @NotNull
  @Override
  public List<LogtalkVarDeclaration> getVarDeclarations() {
    return LogtalkResolveUtil.getClassVarDeclarations(this);
  }

  @Nullable
  @Override
  public LogtalkNamedComponent findLogtalkFieldByName(@NotNull final String name) {
    return ContainerUtil.find(getLogtalkFields(), new Condition<LogtalkNamedComponent>() {
      @Override
      public boolean value(LogtalkNamedComponent component) {
        return name.equals(component.getName());
      }
    });
  }

  @Override
  public LogtalkNamedComponent findLogtalkMethodByName(@NotNull final String name) {
    return ContainerUtil.find(getLogtalkMethods(), new Condition<LogtalkNamedComponent>() {
      @Override
      public boolean value(LogtalkNamedComponent component) {
        return name.equals(component.getName());
      }
    });
  }

  @Nullable
  @Override
  public LogtalkNamedComponent findArrayAccessGetter() {
    LogtalkNamedComponent accessor = ContainerUtil.find(getLogtalkMethods(), new Condition<LogtalkNamedComponent>() {
      @Override
      public boolean value(LogtalkNamedComponent component) {
        if (component instanceof LogtalkMethod) {
          LogtalkMethodModel model = ((LogtalkMethod)component).getModel();
          return model != null && model.isArrayAccessor() && model.getParameterCount() == 1;
        }
        return false;
      }
    });
    // Maybe old style getter?
    if (null == accessor) {
      accessor = findLogtalkMethodByName("__get");
    }
    return accessor;
  }

  @Override
  public boolean isGeneric() {
    return getGenericParam() != null;
  }

  @Override
  public boolean isEnum() {
    return (LogtalkComponentType.typeOf(this) == LogtalkComponentType.ENUM) || this.isAbstract() && hasMeta("@:enum");
  }

  @Override
  public boolean isAnnotationType() {
    /* both: annotation & typedef in logtalk are treated as typedef! */
    return (LogtalkComponentType.typeOf(this) == LogtalkComponentType.TYPEDEF);
  }

  @Override
  public boolean isDeprecated() {
    /* not applicable to Logtalk language */
    return false;
  }

  @Override
  @NotNull
  public PsiClass[] getSupers() {
    // Extends and Implements in one list
    return PsiClassImplUtil.getSupers(this);
  }

  @Override
  public PsiClass getSuperClass() {
    return PsiClassImplUtil.getSuperClass(this);
  }

  @Override
  @NotNull
  public PsiClassType[] getSuperTypes() {
    return PsiClassImplUtil.getSuperTypes(this);
  }

  @Override
  public PsiElement getScope() {
    String name = this.getName();
    if (null == name || "".equals(name)) {
      // anonymous class inherits containing class' search scope
      return this.getContainingClass();
    }
    return this.getContainingFile();
  }

  @Override
  public PsiClass getContainingClass() {
    PsiElement parent = getParent();
    return (parent instanceof PsiClass ? (PsiClass)parent : null);
  }

  @Override
  public PsiClass[] getInterfaces() {  // Extends and Implements in one list
    return PsiClassImplUtil.getInterfaces(this);
  }

  @Override
  @Nullable
  public PsiReferenceList getExtendsList() {
    // LOG.debug("\n>>>\tgetExtendsList();");
    LogtalkInheritList inh = PsiTreeUtil.getChildOfType(this, LogtalkInheritList.class);
    return null == inh ? null : PsiTreeUtil.getChildOfType(inh, LogtalkExtendsDeclaration.class);
  }

  @Override
  @NotNull
  public PsiClassType[] getExtendsListTypes() {
    final PsiReferenceList extendsList = this.getExtendsList();
    if (extendsList != null) {
      return extendsList.getReferencedTypes();
    }
    return PsiClassType.EMPTY_ARRAY;
  }

  @Override
  @Nullable
  public PsiReferenceList getImplementsList() {
    LogtalkInheritList inh = PsiTreeUtil.getChildOfType(this, LogtalkInheritList.class);
    return null == inh ? null : PsiTreeUtil.getChildOfType(inh, LogtalkImplementsDeclaration.class);
  }

  @Override
  @NotNull
  public PsiClassType[] getImplementsListTypes() {
    final PsiReferenceList implementsList = this.getImplementsList();
    if (implementsList != null) {
      return implementsList.getReferencedTypes();
    }
    return PsiClassType.EMPTY_ARRAY;
  }

  @Override
  public boolean isInheritor(@NotNull PsiClass baseClass, boolean checkDeep) {
    return InheritanceImplUtil.isInheritor(this, baseClass, checkDeep);
  }

  @Override
  public boolean isInheritorDeep(PsiClass baseClass, @Nullable PsiClass classToByPass) {
    return InheritanceImplUtil.isInheritorDeep(this, baseClass, classToByPass);
  }

  @Override
  @NotNull
  public PsiClassInitializer[] getInitializers() {
    // XXX: This may be needed during implementation of refactoring feature
    // Needs change in BNF to detect initializer patterns, load them as accessible constructs in a class object
    // For now, this will be empty
    return PsiClassInitializer.EMPTY_ARRAY;
  }

  @Override
  @NotNull
  public LogtalkPsiField[] getFields() {
    List<LogtalkNamedComponent> haxeFields = getLogtalkFields();
    LogtalkPsiField[] psiFields = new LogtalkPsiField[haxeFields.size()];
    return haxeFields.toArray(psiFields);
  }

  @Override
  @NotNull
  public PsiField[] getAllFields() {
    return PsiClassImplUtil.getAllFields(this);
  }

  @Override
  @Nullable
  public PsiField findFieldByName(@NonNls String name, boolean checkBases) {
    return PsiClassImplUtil.findFieldByName(this, name, checkBases);
  }

  @Override
  @NotNull
  public PsiMethod[] getMethods() {
    final List<LogtalkNamedComponent> alltypes = LogtalkResolveUtil.getNamedSubComponents(this);
    final List<LogtalkNamedComponent> methods = LogtalkResolveUtil.filterNamedComponentsByType(alltypes, LogtalkComponentType.METHOD);
    return methods.toArray(PsiMethod.EMPTY_ARRAY); // size is irrelevant
  }

  @Override
  @NotNull
  public PsiMethod[] getAllMethods() {
    return PsiClassImplUtil.getAllMethods(this);
  }

  @Override
  @NotNull
  public PsiMethod[] getConstructors() {
    return PsiClassImplUtil.findMethodsByName(this, LogtalkTokenTypes.ONEW.toString(), false);
  }

  @Override
  @Nullable
  public PsiMethod findMethodBySignature(final PsiMethod psiMethod, final boolean checkBases) {
    return PsiClassImplUtil.findMethodBySignature(this, psiMethod, checkBases);
  }

  @Override
  @NotNull
  public PsiMethod[] findMethodsByName(@NonNls String name, boolean checkBases) {
    if ("main".equals(name)) { checkBases = false; }
    return PsiClassImplUtil.findMethodsByName(this, name, checkBases);
  }

  @Override
  @NotNull
  public PsiMethod[] findMethodsBySignature(PsiMethod patternMethod, boolean checkBases) {
    return PsiClassImplUtil.findMethodsBySignature(this, patternMethod, checkBases);
  }

  @Override
  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
    return PsiClassImplUtil.getAllWithSubstitutorsByMap(this, PsiClassImplUtil.MemberType.METHOD);
  }

  @Override
  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(@NonNls String name, boolean checkBases) {
    return PsiClassImplUtil.findMethodsAndTheirSubstitutorsByName(this, name, checkBases);
  }

  @Override
  public boolean hasTypeParameters() {
    return PsiImplUtil.hasTypeParameters(this);
  }

  @Override
  @Nullable
  public PsiTypeParameterList getTypeParameterList() {
    return new PsiTypeParameterListImpl(this.getNode());
  }

  @Override
  @NotNull
  public PsiTypeParameter[] getTypeParameters() {
    return PsiImplUtil.getTypeParameters(this);
  }

  @Override
  public PsiElement getLBrace() {
    return findChildByRoleAsPsiElement(ChildRole.LBRACE);
  }

  @Override
  public PsiElement getRBrace() {
    return findChildByRoleAsPsiElement(ChildRole.RBRACE);
  }

  private boolean isPrivate() {
    LogtalkPrivateKeyWord privateKeyWord = null;
    if (this instanceof LogtalkClassDeclaration) { // concrete class
      privateKeyWord = ((LogtalkClassDeclaration)this).getPrivateKeyWord();
    } else if (this instanceof LogtalkAbstractClassDeclaration) { // abstract class
      privateKeyWord = ((LogtalkAbstractClassDeclaration)this).getPrivateKeyWord();
    } else if (this instanceof LogtalkExternClassDeclaration) { // extern class
      privateKeyWord = ((LogtalkExternClassDeclaration)this).getPrivateKeyWord();
    } else if (this instanceof LogtalkTypedefDeclaration) { // typedef
      privateKeyWord = ((LogtalkTypedefDeclaration)this).getPrivateKeyWord();
    } else if (this instanceof LogtalkInterfaceDeclaration) { // interface
      privateKeyWord = ((LogtalkInterfaceDeclaration)this).getPrivateKeyWord();
    } else if (this instanceof LogtalkEnumDeclaration) { // enum
      privateKeyWord = ((LogtalkEnumDeclaration)this).getPrivateKeyWord();
    }
    return (privateKeyWord != null);
  }

  @Override
  public boolean isPublic() {
    return !isPrivate();
  }

  @NotNull
  @Override
  public LogtalkModifierList getModifierList() {

    LogtalkModifierList list = super.getModifierList();

    if (null == list) {
      list = new LogtalkModifierListImpl(this.getNode());
    }

    // -- below modifiers need to be set individually
    //    because, they cannot be enforced through macro-list

    if (isPrivate()) {
      list.setModifierProperty(LogtalkPsiModifier.PRIVATE, true);
    }

    if (this instanceof LogtalkAbstractClassDeclaration) { // is abstract class
      list.setModifierProperty(LogtalkPsiModifier.ABSTRACT, true);
    }

    // XXX: Users of LogtalkModifierList generally check for the existence of the property, not it's value.
    //      So, don't set it.
    //list.setModifierProperty(LogtalkPsiModifier.STATIC, false); // Logtalk does not have static classes, yet!
    LOG.assertTrue(!list.hasModifierProperty(LogtalkPsiModifier.STATIC), "Logtalk classes cannot be static.");

    return list;
  }

  @Override
  public boolean hasModifierProperty(@LogtalkPsiModifier.ModifierConstant @NonNls @NotNull String name) {
    return this.getModifierList().hasModifierProperty(name);
  }

  @Override
  @Nullable
  public PsiDocComment getDocComment() {
    // TODO: Fix 'public PsiDocComment getDocComment()'
    //PsiComment psiComment = LogtalkResolveUtil.findDocumentation(this);
    //return ((psiComment != null)? new LogtalkPsiDocComment(this, psiComment) : null);
    return null;
  }

  @Override
  @NotNull
  public PsiElement getNavigationElement() {
    return this;
  }

  @Override
  @Nullable
  public PsiIdentifier getNameIdentifier() {
    // For a LogtalkClass, the identifier is three children below.  The first is
    // the component name, then a reference, and finally the identifier.
    LogtalkComponentName name = PsiTreeUtil.getChildOfType(this, LogtalkComponentName.class);
    return null == name ? null : name.getIdentifier();
  }

  @Override
  @NotNull
  public Collection<HierarchicalMethodSignature> getVisibleSignatures() {
    return PsiSuperMethodImplUtil.getVisibleSignatures(this);
  }

  @Override
  @NotNull
  public PsiClass[] getInnerClasses() {
    return PsiClass.EMPTY_ARRAY;
  }

  @Override
  @NotNull
  public PsiClass[] getAllInnerClasses() {
    return PsiClass.EMPTY_ARRAY;
  }

  @Override
  @Nullable
  public PsiClass findInnerClassByName(@NonNls String name, boolean checkBases) {
    return null;
  }


  public static AbstractLogtalkPsiClass EMPTY_FACADE = new AbstractLogtalkPsiClass(new LogtalkDummyASTNode("EMPTY_FACADE")) {
    @Nullable
    @Override
    public LogtalkGenericParam getGenericParam() {
      return null;
    }

    @Nullable
    @Override
    public LogtalkComponentName getComponentName() {
      return null;
    }
  };

  @Override
  public void delete() {
    // FIX: for twice deletion of file in project view (issue #424)
    final LogtalkFile file = (LogtalkFile)getContainingFile();
    super.delete();
    if (file != null && file.getClasses().length == 0) {
      file.delete();
    }
  }
}
