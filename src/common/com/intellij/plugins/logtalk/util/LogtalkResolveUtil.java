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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.plugins.logtalk.LogtalkComponentType;
import com.intellij.plugins.logtalk.lang.psi.*;
import com.intellij.plugins.logtalk.lang.psi.impl.AbstractLogtalkTypeDefImpl;
import com.intellij.plugins.logtalk.lang.psi.impl.LogtalkReferenceExpression;
import com.intellij.plugins.logtalk.model.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashSet;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkResolveUtil {
  private static final LogtalkDebugLogger LOG = LogtalkDebugLogger.getLogger();

  static {
    LOG.setLevel(Level.INFO);
  }  // We want warnings to get out to the log.

  @Nullable
  public static LogtalkReference getLeftReference(@Nullable final PsiElement node) {
    if (node == null) {
      return null;
    }

    PsiElement expression = node.getFirstChild();

    if (expression instanceof LogtalkReference) {
      return (LogtalkReference)expression;
    }
    if (expression instanceof LogtalkParenthesizedExpression && ((LogtalkParenthesizedExpression)expression).getTypeCheckExpr() != null) {
      return ((LogtalkParenthesizedExpression)expression).getTypeCheckExpr();
    }

    return null;
  }

  @NotNull
  public static Pair<String, String> splitQName(@NotNull String qName) {
    final int dotIndex = qName.lastIndexOf('.');
    final String packageName = dotIndex == -1 ? "" : qName.substring(0, dotIndex);
    final String className = dotIndex == -1 ? qName : qName.substring(dotIndex + 1);

    return Pair.create(packageName, className);
  }

  @NotNull
  public static String joinQName(@Nullable String packageName, @Nullable String className) {
    String result = "";
    if (packageName != null && !packageName.isEmpty()) {
      result = packageName;
      if (className != null) {
        result += ".";
      }
    }
    if (className != null) {
      result += className;
    }
    return result;
  }

  @NotNull
  @NonNls
  public static String getPackageName(@Nullable final PsiFile file) {
    final LogtalkPackageStatement packageStatement = PsiTreeUtil.getChildOfType(file, LogtalkPackageStatement.class);
    return getPackageName(packageStatement);
  }

  @NotNull
  @NonNls
  public static String getPackageName(@Nullable LogtalkPackageStatement packageStatement) {
    LogtalkReferenceExpression referenceExpression = packageStatement != null ? packageStatement.getReferenceExpression() : null;
    if (referenceExpression != null) {
      return referenceExpression.getText();
    }
    return "";
  }

  @Nullable
  public static LogtalkClass findClassByQName(final @Nullable String qName, final @Nullable PsiElement context) {
    if (context == null || qName == null) {
      return null;
    }
    final PsiManager psiManager = context.getManager();
    final GlobalSearchScope scope = getScopeForElement(context);
    return findClassByQName(qName, psiManager, scope);
  }

  @NotNull
  public static GlobalSearchScope getScopeForElement(@NotNull PsiElement context) {
    final Project project = context.getProject();
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      return GlobalSearchScope.allScope(project);
    }
    final Module module = ModuleUtilCore.findModuleForPsiElement(context);
    return module != null ? GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module) : GlobalSearchScope.allScope(project);
  }

  @Nullable
  public static LogtalkClass findClassByQName(String qName, PsiManager psiManager, GlobalSearchScope scope) {
    final FullyQualifiedInfo qualifiedInfo = new FullyQualifiedInfo(qName);
    List<LogtalkModel> result = LogtalkProjectModel.fromProject(psiManager.getProject()).resolve(qualifiedInfo, scope);
    if (result != null && !result.isEmpty()) {
      LogtalkModel item = result.get(0);
      if (item instanceof LogtalkFileModel) {
        LogtalkClassModel classModel = ((LogtalkFileModel)item).getMainClassModel();
        return classModel != null ? classModel.logtalkClass : null;
      }
      if (item instanceof LogtalkClassModel) {
        return ((LogtalkClassModel)item).logtalkClass;
      }
    }

    return null;
  }

  @NotNull
  public static List<LogtalkClass> findComponentDeclarations(@Nullable PsiFile file) {
    if (file == null) {
      return Collections.emptyList();
    }
    final LogtalkClass[] components = PsiTreeUtil.getChildrenOfType(file, LogtalkClass.class);
    if (components == null) {
      return Collections.emptyList();
    }
    return Arrays.asList(components);
  }

  @Nullable
  public static LogtalkClass findComponentDeclaration(@Nullable PsiFile file, @NotNull String componentName) {
    final List<LogtalkClass> declarations = findComponentDeclarations(file);
    for (LogtalkClass logtalkClass : declarations) {
      final LogtalkComponentName identifier = logtalkClass.getComponentName();
      if (identifier != null && componentName.equals(identifier.getText())) {
        return logtalkClass;
      }
    }
    return null;
  }

  @NotNull
  public static List<LogtalkType> findExtendsList(@Nullable LogtalkInheritList extendsList) {
    List<? extends LogtalkInherit> ext = null == extendsList ? null : extendsList.getExtendsDeclarationList();
    return findExtendsImplementsListImpl(ext);
  }

  public static List<LogtalkType> getImplementsList(@Nullable LogtalkInheritList extendsList) {
    List<? extends LogtalkInherit> ext = null == extendsList ? null : extendsList.getImplementsDeclarationList();
    return findExtendsImplementsListImpl(ext);
  }

  @NotNull
  private static List<LogtalkType> findExtendsImplementsListImpl(@Nullable List<? extends LogtalkInherit> extendsList) {
    if (extendsList == null) {
      return Collections.emptyList();
    }
    final List<LogtalkType> result = new ArrayList<LogtalkType>();
    for (LogtalkInherit inherit : extendsList) {
      final List<LogtalkType> inheritTypes = inherit.getTypeList();
      result.addAll(inheritTypes);
    }
    return result;
  }

  public static List<LogtalkNamedComponent> filterNamedComponentsByType(List<LogtalkNamedComponent> result, final LogtalkComponentType type) {
    return ContainerUtil.filter(result, new Condition<LogtalkNamedComponent>() {
      @Override
      public boolean value(LogtalkNamedComponent component) {
        return LogtalkComponentType.typeOf(component) == type;
      }
    });
  }

  @Nullable
  public static LogtalkNamedComponent findNamedSubComponent(@Nullable LogtalkClass logtalkClass, @NotNull final String name) {
    if (logtalkClass == null) {
      return null;
    }
    final LogtalkNamedComponent result = logtalkClass.findLogtalkMethodByName(name);
    return result != null ? result : logtalkClass.findLogtalkFieldByName(name);
  }

  @NotNull
  public static List<LogtalkNamedComponent> findNamedSubComponents(@NotNull LogtalkClass... rootLogtalkClasses) {
    return findNamedSubComponents(true, rootLogtalkClasses);
  }

  @NotNull
  public static List<LogtalkNamedComponent> findNamedSubComponents(boolean unique, @NotNull LogtalkClass... rootLogtalkClasses) {
    final List<LogtalkNamedComponent> unfilteredResult = new ArrayList<LogtalkNamedComponent>();
    final LinkedList<LogtalkClass> classes = new LinkedList<LogtalkClass>();
    final HashSet<LogtalkClass> processed = new HashSet<LogtalkClass>();
    classes.addAll(Arrays.asList(rootLogtalkClasses));
    while (!classes.isEmpty()) {
      final LogtalkClass logtalkClass = classes.pollFirst();
      for (LogtalkNamedComponent namedComponent : getNamedSubComponents(logtalkClass)) {
        if (namedComponent.getName() != null) {
          unfilteredResult.add(namedComponent);
        }
      }

      List<LogtalkType> baseTypes = new ArrayList<LogtalkType>();
      baseTypes.addAll(logtalkClass.getLogtalkExtendsList());
      baseTypes.addAll(logtalkClass.getLogtalkImplementsList());
      List<LogtalkClass> baseClasses = tyrResolveClassesByQName(baseTypes);
      for (LogtalkClass baseClass : baseClasses) {
        if (processed.add(baseClass)) {
          classes.add(baseClass);
        }
      }
    }
    if (!unique) {
      return unfilteredResult;
    }

    return new ArrayList<LogtalkNamedComponent>(namedComponentToMap(unfilteredResult).values());
  }

  public static Map<String, LogtalkNamedComponent> namedComponentToMap(List<LogtalkNamedComponent> unfilteredResult) {
    final Map<String, LogtalkNamedComponent> result = new HashMap<String, LogtalkNamedComponent>();
    for (LogtalkNamedComponent logtalkNamedComponent : unfilteredResult) {
      // need order
      if (result.containsKey(logtalkNamedComponent.getName())) continue;
      result.put(logtalkNamedComponent.getName(), logtalkNamedComponent);
    }
    return result;
  }

  @NotNull
  public static List<LogtalkNamedComponent> getNamedSubComponentsInOrder(LogtalkClass logtalkClass) {
    final List<LogtalkNamedComponent> result = getNamedSubComponents(logtalkClass);
    Collections.sort(result, new Comparator<LogtalkNamedComponent>() {
      @Override
      public int compare(LogtalkNamedComponent o1, LogtalkNamedComponent o2) {
        return o1.getTextOffset() - o2.getTextOffset();
      }
    });
    return result;
  }

  public static List<LogtalkNamedComponent> getNamedSubComponents(LogtalkClass logtalkClass) {
    PsiElement body = null;
    final LogtalkComponentType type = LogtalkComponentType.typeOf(logtalkClass);
    if (type == LogtalkComponentType.CLASS) {
      body = PsiTreeUtil.getChildOfAnyType(logtalkClass, LogtalkClassBody.class, LogtalkExternClassDeclarationBody.class);
    } else if (type == LogtalkComponentType.INTERFACE) {
      body = PsiTreeUtil.getChildOfType(logtalkClass, LogtalkInterfaceBody.class);
    } else if (type == LogtalkComponentType.ENUM) {
      body = PsiTreeUtil.getChildOfType(logtalkClass, LogtalkEnumBody.class);
    } else if (logtalkClass instanceof LogtalkTypedefDeclaration) {
      final LogtalkTypeOrAnonymous typeOrAnonymous = getFirstItem(((LogtalkTypedefDeclaration)logtalkClass).getTypeOrAnonymousList());
      if (typeOrAnonymous != null && typeOrAnonymous.getAnonymousType() != null) {
        LogtalkAnonymousType anonymous = typeOrAnonymous.getAnonymousType();
        if (anonymous != null) {
          return getNamedSubComponents(anonymous);
        }
      } else if (typeOrAnonymous != null) {
        final LogtalkClass typeClass = getLogtalkClassResolveResult(typeOrAnonymous.getType()).getLogtalkClass();
        assert typeClass != logtalkClass;
        return getNamedSubComponents(typeClass);
      }
    }

    final List<LogtalkNamedComponent> result = new ArrayList<LogtalkNamedComponent>();
    if (logtalkClass instanceof LogtalkAnonymousType) {
      final LogtalkAnonymousTypeFieldList typeFieldList = ((LogtalkAnonymousType)logtalkClass).getAnonymousTypeBody().getAnonymousTypeFieldList();
      if (typeFieldList != null) {
        result.addAll(typeFieldList.getAnonymousTypeFieldList());
      }
      body = ((LogtalkAnonymousType)logtalkClass).getAnonymousTypeBody().getInterfaceBody();
    }
    if (body == null) {
      return result;
    }
    final LogtalkNamedComponent[] namedComponents = PsiTreeUtil.getChildrenOfType(body, LogtalkNamedComponent.class);
    if (namedComponents != null) {
      result.addAll(Arrays.asList(namedComponents));
    }


    return result;
  }

  public static List<LogtalkVarDeclaration> getClassVarDeclarations(LogtalkClass logtalkClass) {
    PsiElement body = null;
    final LogtalkComponentType type = LogtalkComponentType.typeOf(logtalkClass);
    if (type == LogtalkComponentType.CLASS) {
      body = PsiTreeUtil.getChildOfAnyType(logtalkClass, LogtalkClassBody.class, LogtalkExternClassDeclarationBody.class);
    }

    final List<LogtalkVarDeclaration> result = new ArrayList<LogtalkVarDeclaration>();

    if (body == null) {
      return result;
    }

    final LogtalkVarDeclaration[] variables = PsiTreeUtil.getChildrenOfType(body, LogtalkVarDeclaration.class);

    if (variables == null) {
      return result;
    }
    Collections.addAll(result, variables);
    return result;
  }

  @NotNull
  public static LogtalkClassResolveResult getLogtalkClassResolveResult(@Nullable PsiElement element) {
    return getLogtalkClassResolveResult(element, new LogtalkGenericSpecialization());
  }

  private static ThreadLocal<Stack<PsiElement>> resolveStack = new ThreadLocal<Stack<PsiElement>>() {
    @Override
    protected Stack<PsiElement> initialValue() {
      return new Stack<PsiElement>();
    }
  };

  @NotNull
  public static LogtalkClassResolveResult getLogtalkClassResolveResult(@Nullable PsiElement element,
                                                                 @NotNull LogtalkGenericSpecialization specialization) {
    if (element == null || element instanceof PsiPackage) {
      return LogtalkClassResolveResult.EMPTY;
    }

    final Stack<PsiElement> stack = resolveStack.get();
    if (stack.search(element) > 0) {
      // We're already trying to resolve this element.  Prevent stack overflow.
      LOG.warn("Cannot resolve recursive/cyclic definition of " + element.getText()
               + "found at " + LogtalkDebugUtil.elementLocation(element));
      return LogtalkClassResolveResult.EMPTY;
    }

    try {
      stack.push(element);

      if (element instanceof LogtalkComponentName) {
        return getLogtalkClassResolveResult(element.getParent(), specialization);
      }
      if (element instanceof AbstractLogtalkTypeDefImpl) {
        final AbstractLogtalkTypeDefImpl typeDef = (AbstractLogtalkTypeDefImpl)element;
        return typeDef.getTargetClass(specialization);
      }
      if (element instanceof LogtalkClass) {
        final LogtalkClass logtalkClass = (LogtalkClass)element;
        return LogtalkClassResolveResult.create(logtalkClass);
      }
      if (element instanceof LogtalkForStatement) {
        final LogtalkIterable iterable = ((LogtalkForStatement)element).getIterable();
        if (iterable == null) {
          // iterable is @Nullable
          // (sometimes when you're typing for statement it becames null for short time)
          return LogtalkClassResolveResult.EMPTY;
        }
        final LogtalkExpression expression = iterable.getExpression();
        if (expression instanceof LogtalkReference) {
          final LogtalkClassResolveResult resolveResult = ((LogtalkReference)expression).resolveLogtalkClass();
          final LogtalkClass resolveResultLogtalkClass = resolveResult.getLogtalkClass();
          // try next
          LogtalkClassResolveResult result =
            getLogtalkClassResolveResult(resolveResultLogtalkClass == null ? null : resolveResultLogtalkClass.findLogtalkMethodByName("next"),
                                      resolveResult.getSpecialization());
          if (result.getLogtalkClass() != null) {
            return result;
          }
          // try iterator
          LogtalkClassResolveResult iteratorResult =
            getLogtalkClassResolveResult(resolveResultLogtalkClass == null ? null : resolveResultLogtalkClass.findLogtalkMethodByName("iterator"),
                                      resolveResult.getSpecialization().getInnerSpecialization(resolveResultLogtalkClass));
          LogtalkClass iteratorResultLogtalkClass = iteratorResult.getLogtalkClass();
          // Now, look for iterator's next
          result =
            getLogtalkClassResolveResult(iteratorResultLogtalkClass == null ? null : iteratorResultLogtalkClass.findLogtalkMethodByName("next"),
                                      iteratorResult.getSpecialization());

          return result;
        }
        return LogtalkClassResolveResult.EMPTY;
      }

      LogtalkClassResolveResult result = tryResolveClassByTypeTag(element, specialization);
      if (result.getLogtalkClass() != null) {
        return result;
      }

      result = LogtalkAbstractEnumUtil.resolveFieldType(element);
      if (result != null) {
        return result;
      }

      if (specialization.containsKey(null, element.getText())) {
        return specialization.get(null, element.getText());
      }
      final LogtalkVarInit varInit = PsiTreeUtil.getChildOfType(element, LogtalkVarInit.class);
      final LogtalkExpression initExpression = varInit == null ? null : varInit.getExpression();
      if (initExpression instanceof LogtalkReference) {
        result = ((LogtalkReference)initExpression).resolveLogtalkClass();
        result.specialize(initExpression);
        return result;
      }
      return getLogtalkClassResolveResult(initExpression);
    }
    finally {
      try {
        stack.pop();
      }
      catch (EmptyStackException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Unexpected excessive stack pop. " + e.toString());
        }
      }
    }
  }

  @NotNull
  public static LogtalkClassResolveResult tryResolveClassByTypeTag(PsiElement element,
                                                                LogtalkGenericSpecialization specialization) {
    final LogtalkTypeTag typeTag = PsiTreeUtil.getChildOfType(element, LogtalkTypeTag.class);
    final LogtalkTypeOrAnonymous typeOrAnonymous = (typeTag != null) ? getFirstItem(typeTag.getTypeOrAnonymousList()) : null;
    final LogtalkType type = (typeOrAnonymous != null) ? typeOrAnonymous.getType() :
                          ((element instanceof LogtalkType) ? (LogtalkType)element : null);

    LogtalkClass logtalkClass = type == null ? null : tryResolveClassByQName(type);
    if (logtalkClass == null && type != null && specialization.containsKey(element, type.getText())) {
      return specialization.get(element, type.getText());
    }

    if (logtalkClass instanceof LogtalkTypedefDeclaration) {
      LogtalkClassResolveResult temp = LogtalkClassResolveResult.create(logtalkClass, specialization);
      temp.specializeByParameters(type.getTypeParam());
      specialization = temp.getSpecialization();
    }

    LogtalkClassResolveResult result = getLogtalkClassResolveResult(logtalkClass, specialization.getInnerSpecialization(element));
    if (result.getLogtalkClass() != null) {
      result.specializeByParameters(type == null ? null : type.getTypeParam());
      return result;
    }

    if (typeTag != null) {
      return tryResolveFunctionType(getFirstItem(typeTag.getFunctionTypeList()), specialization);
    }

    return LogtalkClassResolveResult.EMPTY;
  }

  private static LogtalkClassResolveResult tryResolveFunctionType(@Nullable LogtalkFunctionType functionType,
                                                               LogtalkGenericSpecialization specialization) {
    if (functionType == null) {
      return LogtalkClassResolveResult.EMPTY;
    }
    final LogtalkTypeOrAnonymous returnTypeOrAnonymous =
      functionType.getTypeOrAnonymousList().get(functionType.getTypeOrAnonymousList().size() - 1);
    final LogtalkClassResolveResult result = tryResolveClassByTypeTag(returnTypeOrAnonymous.getType(), specialization);
    while (functionType != null) {
      // todo: anonymous types :(
      final List<LogtalkTypeOrAnonymous> typeList = functionType.getTypeOrAnonymousList();
      Collections.reverse(typeList);
      for (LogtalkTypeOrAnonymous typeOrAnonymous : typeList) {
        result.getFunctionTypes().add(tryResolveClassByTypeTag(typeOrAnonymous.getType(), specialization));
      }
      functionType = functionType.getFunctionType();
    }
    Collections.reverse(result.getFunctionTypes());
    return result;
  }

  @NotNull
  public static List<LogtalkClass> tyrResolveClassesByQName(@NotNull List<LogtalkType> types) {
    final List<LogtalkClass> result = new ArrayList<LogtalkClass>();
    for (LogtalkType logtalkType : types) {
      final LogtalkClass logtalkClass = tryResolveClassByQName(logtalkType);
      if (logtalkClass != null) {
        result.add(logtalkClass);
      }
    }
    return result;
  }

  @Nullable
  public static LogtalkClass tryResolveClassByQName(@Nullable PsiElement type) {
    if (type == null || type.getContext() == null) {
      return null;
    }

    final String name = getQName(type);
    LogtalkClass result = name == null ? tryResolveClassByQNameWhenGetQNameFail(type) : findClassByQName(name, type.getContext());
    result = result != null ? result : findClassByQNameInSuperPackages(type);
    return result;
  }

  private static String tryResolveFullyQualifiedLogtalkReferenceExpression(PsiElement type) {
    if (type instanceof LogtalkReferenceExpression) {
      LogtalkReferenceExpression topmostParentOfType = PsiTreeUtil.getTopmostParentOfType(type, LogtalkReferenceExpression.class);

      if (topmostParentOfType == null) {
        topmostParentOfType = (LogtalkReferenceExpression)type;
      }

      LogtalkClass logtalkClass = findClassByQName(topmostParentOfType.getText(), topmostParentOfType.getContext());
      if (logtalkClass != null) {
        return topmostParentOfType.getText();
      }

      PsiElement parent = type.getParent();
      LogtalkClass classByQName = findClassByQName(parent.getText(), parent.getContext());
      if (classByQName != null) {
        return parent.getText();
      }
    }

    return null;
  }

  @Nullable
  private static LogtalkClass findClassByQNameInSuperPackages(PsiElement type) {
    LogtalkPackageStatement packageStatement = PsiTreeUtil.getChildOfType(type.getContainingFile(), LogtalkPackageStatement.class);
    String packageName = getPackageName(packageStatement);
    String[] packages = packageName.split("\\.");
    String typeName = (type instanceof LogtalkType ? ((LogtalkType)type).getReferenceExpression() : type).getText();
    for (int i = packages.length - 1; i >= 0; --i) {
      StringBuilder qNameBuilder = new StringBuilder();
      for (int j = 0; j <= i; ++j) {
        if (!packages[j].isEmpty()) {
          qNameBuilder.append(packages[j]).append('.');
        }
      }
      qNameBuilder.append(typeName);
      LogtalkClass logtalkClass = findClassByQName(qNameBuilder.toString(), type);
      if (logtalkClass != null) {
        return logtalkClass;
      }
    }
    return null;
  }

  @Nullable
  private static String getQName(@NotNull PsiElement type) {
    LogtalkImportStatement importStatement = PsiTreeUtil.getParentOfType(type, LogtalkImportStatement.class, false);
    if (importStatement != null) {
      LogtalkReferenceExpression referenceExpression = importStatement.getReferenceExpression();
      return referenceExpression == null ? null : referenceExpression.getText();
    }

    LogtalkUsingStatement usingStatement = PsiTreeUtil.getParentOfType(type, LogtalkUsingStatement.class, false);
    if (usingStatement != null) {
      LogtalkReferenceExpression expression = usingStatement.getReferenceExpression();
      return expression == null ? null : expression.getText();
    }

    return null;
  }

  @Nullable
  private static LogtalkClass tryResolveClassByQNameWhenGetQNameFail(@NotNull PsiElement type) {
    if (type instanceof LogtalkType) {
      type = ((LogtalkType)type).getReferenceExpression();
    }

    String className = type.getText();
    PsiElement result = null;

    if (className != null && className.indexOf('.') == -1) {
      final LogtalkFileModel fileModel = LogtalkFileModel.fromElement(type);
      if (fileModel != null) {
        result = searchInSameFile(fileModel, className);
        if (result == null) result = searchInImports(fileModel, className);
        if (result == null) result = searchInSamePackage(fileModel, className);
      }
    } else {
      className = tryResolveFullyQualifiedLogtalkReferenceExpression(type);
      result = findClassByQName(className, type.getContext());
    }

    result = result != null ? result : findClassByQName(className, type.getContext());

    return result instanceof LogtalkClass ? (LogtalkClass)result : null;
  }

  @Nullable
  public static PsiElement searchInSameFile(@NotNull LogtalkFileModel file, @NotNull String name) {
    List<LogtalkClassModel> models = file.getClassModels();
    final Stream<LogtalkClassModel> classesStream = models.stream().filter(model -> name.equals(model.getName()));
    final Stream<LogtalkEnumValueModel> enumsStream = models.stream().filter(model -> model instanceof LogtalkEnumModel)
      .map(model -> ((LogtalkEnumModel)model).getValue(name))
      .filter(Objects::nonNull);

    final LogtalkModel result = Stream.concat(classesStream, enumsStream)
      .findFirst()
      .orElse(null);

    return result != null ? result.getBasePsi() : null;
  }

  @Nullable
  public static PsiElement searchInImports(LogtalkFileModel file, String name) {
    LogtalkImportModel importModel = StreamUtil.reverse(file.getImportModels().stream())
      .filter(model -> {
        PsiElement exposedItem = model.exposeByName(name);
        return exposedItem != null;
      })
      .findFirst().orElse(null);

    if (importModel != null) {
      return importModel.exposeByName(name);
    }
    return null;
  }

  @Nullable
  public static PsiElement searchInSamePackage(@NotNull LogtalkFileModel file, @NotNull String name) {
    final LogtalkPackageModel packageModel = file.getPackageModel();
    LogtalkModel result = null;

    if (packageModel != null) {
      result = packageModel.getExposedMembers().stream()
        .filter(model -> name.equals(model.getName()))
        .findFirst()
        .orElse(null);
    }

    return result != null ? result.getBasePsi() : null;
  }

  public static String getQName(PsiElement[] fileChildren, final String result, boolean searchInSamePackage) {
    final LogtalkClass classForType = (LogtalkClass)Arrays.stream(fileChildren)
      .filter(child -> child instanceof LogtalkClass && result.equals(((LogtalkClass)child).getName()))
      .findFirst()
      .orElse(null);

    if (classForType != null) {
      return classForType.getQualifiedName();
    }

    final LogtalkImportStatement importStatement =
      (LogtalkImportStatement)(StreamUtil.reverse(Arrays.stream(fileChildren))
                              .filter(element ->
                                        element instanceof LogtalkImportStatement &&
                                        ((LogtalkImportStatement)element).getModel().exposeByName(result) != null)
                              .findFirst()
                              .orElse(null));

    final LogtalkExpression importStatementExpression = importStatement == null ? null : importStatement.getReferenceExpression();
    if (importStatementExpression != null) {
      return importStatementExpression.getText();
    }

    if (searchInSamePackage && fileChildren.length > 0) {
      final LogtalkFileModel fileModel = LogtalkFileModel.fromElement(fileChildren[0]);
      if (fileModel != null) {
        final LogtalkPackageModel packageModel = fileModel.getPackageModel();
        if (packageModel != null) {
          final LogtalkClassModel classModel = packageModel.getClassModel(result);
          if (classModel != null) {
            return classModel.logtalkClass.getQualifiedName();
          }
        }
      }
    }

    return result;
  }

  @Nullable
  public static PsiComment findDocumentation(LogtalkNamedComponent element) {
    final PsiElement candidate = UsefulPsiTreeUtil.getPrevSiblingSkipWhiteSpaces(element, true);
    if (candidate instanceof PsiComment) {
      return (PsiComment)candidate;
    }
    return null;
  }

  public static Set<IElementType> getDeclarationTypes(@Nullable LogtalkDeclarationAttribute[] attributeList) {
    return attributeList == null ? Collections.<IElementType>emptySet() : getDeclarationTypes(Arrays.asList(attributeList));
  }

  public static Set<IElementType> getDeclarationTypes(@Nullable List<LogtalkDeclarationAttribute> attributeList) {
    if (attributeList == null || attributeList.isEmpty()) {
      return Collections.emptySet();
    }
    final Set<IElementType> resultSet = new THashSet<IElementType>();
    for (LogtalkDeclarationAttribute attribute : attributeList) {
      PsiElement result = attribute.getFirstChild();
      final LogtalkAccess access = attribute.getAccess();
      if (access != null) {
        result = access.getFirstChild();
      }
      if (result instanceof LeafPsiElement) {
        resultSet.add(((LeafPsiElement)result).getElementType());
      }
    }
    return resultSet;
  }

  @NotNull
  public static List<LogtalkComponentName> getComponentNames(List<? extends LogtalkNamedComponent> components) {
    return ContainerUtil.map(components, (Function<LogtalkNamedComponent, LogtalkComponentName>)LogtalkNamedComponent::getComponentName);
  }

  public static HashSet<LogtalkClass> getBaseClassesSet(@NotNull LogtalkClass clazz) {
    return getBaseClassesSet(clazz, new HashSet<LogtalkClass>());
  }

  @NotNull
  public static HashSet<LogtalkClass> getBaseClassesSet(@NotNull LogtalkClass clazz, @NotNull HashSet<LogtalkClass> outClasses) {
    List<LogtalkType> types = new ArrayList<LogtalkType>();
    types.addAll(clazz.getLogtalkExtendsList());
    types.addAll(clazz.getLogtalkImplementsList());
    for (LogtalkType baseType : types) {
      final LogtalkClass baseClass = LogtalkResolveUtil.tryResolveClassByQName(baseType);
      if (baseClass != null && outClasses.add(baseClass)) {
        getBaseClassesSet(baseClass, outClasses);
      }
    }
    return outClasses;
  }
}
