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

import com.intellij.plugins.haxe.model.LogtalkModelTarget;
import com.intellij.psi.PsiField;

/**
 * Created by srikanthg on 10/9/14.
 */
//
// NOTE: This class MUST derive from LogtalkComponent -- not LogtalkNamedComponent for
// certain refactorings (that use LogtalkQualifiedNameProvider) to work correctly.
// If the derivation changes, an exception is thrown out of than class, and
// refactorings requiring names silently fail (becase of a cast exception).
// See testSrc/com/intellij/plugins/logtalk/ide/refactoring/introduce/LogtalkIntroduceVariableTest#ReplaceAll3()
//
//                                    |||||||||||||
//                                    vvvvvvvvvvvvv
public interface LogtalkPsiField extends LogtalkComponent, PsiField, LogtalkModelTarget {

}
