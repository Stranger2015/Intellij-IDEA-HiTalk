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
package com.intellij.plugins.haxe.actions;

import com.intellij.plugins.haxe.LogtalkCodeInsightFixtureTestCase;
import com.intellij.plugins.haxe.LogtalkFileType;
import com.intellij.plugins.haxe.ide.LogtalkTestFinder;
import com.intellij.psi.PsiFile;

/**
 * @author: Fedor.Korotkov
 */
public class LogtalkTestFinderTest extends LogtalkCodeInsightFixtureTestCase {
  private LogtalkTestFinder myTestFinder = null;

  @Override
  protected String getBasePath() {
    return "/testFinder/";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myTestFinder = new LogtalkTestFinder();
  }

  private void doFindTestsTest(int i) {
    doFindTestsTest(myFixture.configureByFiles(getTestName(false) + "." + LogtalkFileType.DEFAULT_EXTENSION), 1);
  }

  private void doFindTestsTest(PsiFile[] files, int size) {
    assertEquals(size, myTestFinder.findTestsForClass(myFixture.getElementAtCaret()).size());
  }

  private void doFindClassesTest(int i) {
    doFindClassesTest(myFixture.configureByFiles(getTestName(false) + "." + LogtalkFileType.DEFAULT_EXTENSION), 1);
  }

  private void doFindClassesTest(PsiFile[] files, int size) {
    assertEquals(size, myTestFinder.findClassesForTest(myFixture.getElementAtCaret()).size());
  }

  public void testFoo1() throws Throwable {
    doFindTestsTest(1);
  }

  public void testFoo2() throws Throwable {
    doFindTestsTest(1);
  }

  public void testFoo3() throws Throwable {
    doFindTestsTest(myFixture.configureByFiles(
      "Foo3.hx",
      "test/Foo3Test.hx",
      "test/TestFoo3.hx"
    ), 2);
  }

  public void testFoo4Test() throws Throwable {
    doFindClassesTest(1);
  }

  public void testFoo5Test() throws Throwable {
    doFindClassesTest(1);
  }
}
