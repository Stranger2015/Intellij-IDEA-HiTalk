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
package com.intellij.plugins.logtalk.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashMap;
import gnu.trove.TObjectObjectProcedure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkGenericSpecialization implements Cloneable {
  public static final LogtalkGenericSpecialization EMPTY = new LogtalkGenericSpecialization();
  final Map<String, LogtalkClassResolveResult> map;

  public LogtalkGenericSpecialization() {
    this(new THashMap<String, LogtalkClassResolveResult>());
  }

  @Override
  protected LogtalkGenericSpecialization clone() {
    final Map<String, LogtalkClassResolveResult> clonedMap = new THashMap<String, LogtalkClassResolveResult>();
    for (String key : map.keySet()) {
      clonedMap.put(key, map.get(key));
    }
    return new LogtalkGenericSpecialization(clonedMap);
  }

  protected LogtalkGenericSpecialization(Map<String, LogtalkClassResolveResult> map) {
    this.map = map;
  }

  public void put(PsiElement element, String genericName, LogtalkClassResolveResult resolveResult) {
    map.put(getGenericKey(element, genericName), resolveResult);
  }

  public boolean containsKey(@Nullable PsiElement element, String genericName) {
    return map.containsKey(getGenericKey(element, genericName));
  }

  public LogtalkGenericSpecialization filterInnerKeys() {
    LogtalkGenericSpecialization filtered = new LogtalkGenericSpecialization();
    for (String key : map.keySet()) {
      if (key.contains("-")) {
        filtered.map.put(key, map.get(key));
      }
    }
    return filtered;
  }

  public LogtalkClassResolveResult get(@Nullable PsiElement element, @NotNull String genericName) {
    return map.get(getGenericKey(element, genericName));
  }

  public LogtalkGenericSpecialization getInnerSpecialization(PsiElement element) {
    final String prefixToRemove = getGenericKey(element, "");
    final Map<String, LogtalkClassResolveResult> result = new THashMap<String, LogtalkClassResolveResult>();
    for (String key : map.keySet()) {
      final LogtalkClassResolveResult value = map.get(key);
      String newKey = key;
      if (newKey.startsWith(prefixToRemove)) {
        newKey = newKey.substring(prefixToRemove.length());
      }
      result.put(newKey, value);
    }
    return new LogtalkGenericSpecialization(result);
  }

  public static String getGenericKey(@Nullable PsiElement element, @NotNull String genericName) {
    final StringBuilder result = new StringBuilder();
    final LogtalkNamedComponent namedComponent = PsiTreeUtil.getParentOfType(element, LogtalkNamedComponent.class, false);
    if (namedComponent instanceof LogtalkClass) {
      result.append(((LogtalkClass)namedComponent).getQualifiedName());
    }
    else if (namedComponent != null) {
      LogtalkClass haxeClass = PsiTreeUtil.getParentOfType(namedComponent, LogtalkClass.class);
      if (haxeClass instanceof LogtalkAnonymousType) {
        // class -> typeOrAnonymous -> anonymous
        final PsiElement parent = haxeClass.getParent().getParent();
        haxeClass = parent instanceof LogtalkClass ? (LogtalkClass)parent : haxeClass;
      }
      if (haxeClass != null) {
        result.append(haxeClass.getQualifiedName());
      }
      if (PsiTreeUtil.getChildOfType(namedComponent, LogtalkGenericParam.class) != null) {
        // generic method
        result.append(":");
        result.append(namedComponent.getName());
      }
    }
    if (result.length() > 0) {
      result.append("-");
    }
    result.append(genericName);
    return result.toString();
  }

  public String debugDump() {
    return debugDump(null);
  }

  public String debugDump( String linePrefix ) {
    StringBuilder builder = new StringBuilder();
    if (linePrefix == null) {
      linePrefix = "";
    }
    builder.append(linePrefix);
    builder.append(getClass().getName());
    builder.append(" : size=");
    builder.append(map.size());
    builder.append("\n");

    String prefix = linePrefix + "    ";

    for (String key : map.keySet()) {
      builder.append(prefix);
      builder.append(key);
      builder.append(" -> ");
      LogtalkClassResolveResult result = map.get(key);
      builder.append(result == null ? "<no value>\n" : result.debugDump(prefix));
    }
    return builder.toString();
  }
}
