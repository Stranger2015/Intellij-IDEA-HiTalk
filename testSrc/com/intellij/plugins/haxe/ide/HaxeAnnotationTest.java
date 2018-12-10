/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2014 AS3Boyan
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
package com.intellij.plugins.haxe.ide;

import com.intellij.lang.LanguageAnnotators;
import com.intellij.plugins.haxe.LogtalkCodeInsightFixtureTestCase;
import com.intellij.plugins.haxe.LogtalkLanguage;
import com.intellij.plugins.haxe.build.ClassWrapper;
import com.intellij.plugins.haxe.build.IdeaTarget;
import com.intellij.plugins.haxe.ide.annotator.LogtalkTypeAnnotator;
import com.intellij.plugins.haxe.ide.inspections.LogtalkUnresolvedSymbolInspection;
import com.intellij.util.ArrayUtil;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkAnnotationTest extends LogtalkCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "/annotation/";
  }

  private void doTest(String... additionalPaths) throws Exception {
    final String[] paths = ArrayUtil.append(additionalPaths, getTestName(false) + ".hx");
    myFixture.configureByFiles(ArrayUtil.reverseArray(paths));
    final LogtalkTypeAnnotator annotator = new LogtalkTypeAnnotator();
    try {
      LanguageAnnotators.INSTANCE.addExplicitExtension(LogtalkLanguage.INSTANCE, annotator);
      myFixture.enableInspections(getAnnotatorBasedInspection());
      try {
        myFixture.testHighlighting(true, true, true, myFixture.getFile().getVirtualFile());
      }
      finally {
        LanguageAnnotators.INSTANCE.removeExplicitExtension(LogtalkLanguage.INSTANCE, annotator);
      }
    } finally {
      LanguageAnnotators.INSTANCE.removeExplicitExtension(LogtalkLanguage.INSTANCE, annotator);
    }
  }

  public void testIDEA_100331() throws Throwable {
    doTest("test/TArray.hx");
  }

  public void testIDEA_100331_2() throws Throwable {
    doTest("test/TArray.hx");
  }

  public void testIDEA_106515() throws Throwable {
    doTest("test/TArray.hx");
  }

  public void testIDEA_106515_2() throws Throwable {
    doTest("test/TArray.hx");
  }

  /* Test that an import file containing no classes of the same name is resolved.
   * The standard logtalk library file logtalk/macro/Tools.hx was the definitive error
   * case.
   *
   * @throws Throwable
   */
  public void testIDEA_ResolveImportWithoutType() throws Throwable {
    final String[] paths = {"test/stdTools.hx", getTestName(false) + ".hx"};
    myFixture.configureByFiles(ArrayUtil.reverseArray(paths));
    final String haxe_macro_Tools_contents = "package logtalk.macro;\ntypedef TExprTools = ExprTools;\n";
    myFixture.addFileToProject("logtalk/macro/Tools.hx", haxe_macro_Tools_contents);
    myFixture.enableInspections(LogtalkUnresolvedSymbolInspection.class);
    myFixture.testHighlighting(true, true, true, myFixture.getFile().getVirtualFile());
  }

}
