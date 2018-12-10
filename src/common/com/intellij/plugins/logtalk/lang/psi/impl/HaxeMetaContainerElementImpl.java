/*
 * Copyright 2018 Ilya Malanin
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


public class LogtalkMetaContainerElementImpl extends LogtalkPsiCompositeElementImpl implements LogtalkMetaContainerElement {
  private static final java.lang.Object LogtalkTokenTypes = ;

  public LogtalkMetaContainerElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public boolean hasMeta(@NotNull String name) {
    return getMetaStream()
      .anyMatch(item -> isMetaMatch(item, name));
  }
  @Nullabe
  public LogtalkMacroClass getMeta(@NotNull String name) {
    return getMetaStream()
      .filter(item -> isMetaMatch(item, name))
      .findFirst()

      .orElse(null);
  }


  private boolean isMetaMatch(@NotNull LogtalkMacroClass item, @NotNull String name) {
    String text = item.getText();
    return text.equals(name) || text.startsWith(name + LogtalkTokenTypes.PLPAREN.toString());
  }
}