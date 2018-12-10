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
package com.intellij.plugins.logtalk.ide.generation;

import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.LogtalkBundle;
import com.intellij.plugins.haxe.lang.psi.LogtalkClass;
import com.intellij.plugins.haxe.lang.psi.LogtalkNamedComponent;
import com.intellij.plugins.haxe.lang.psi.LogtalkVarDeclaration;
import com.intellij.plugins.haxe.lang.psi.impl.AbstractLogtalkNamedComponent;
import com.intellij.plugins.haxe.model.LogtalkClassModel;
import com.intellij.plugins.haxe.model.LogtalkFieldModel;
import com.intellij.plugins.haxe.model.LogtalkMethodModel;
import com.intellij.plugins.haxe.model.LogtalkParameterModel;
import com.intellij.plugins.haxe.model.type.LogtalkTypeResolver;
import com.intellij.plugins.haxe.util.*;
import com.intellij.plugins.logtalk.LogtalkBundle;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogtalkConstructorHandler extends BaseLogtalkGenerateHandler {
  @Override
  protected BaseCreateMethodsFix createFix(final LogtalkClass haxeClass) {
    return new BaseCreateMethodsFix<LogtalkNamedComponent>(haxeClass) {
      @Override
      protected void processElements(Project project, Set<LogtalkNamedComponent> elementsToProcess) {
        LogtalkClassModel clazz = haxeClass.getModel();

        if (clazz.getConstructorSelf() != null) {
          // Constructor already exists
          return;
        }

        LogtalkMethodModel parentConstructor = clazz.getParentConstructor();

        ArrayList<ParamElement> params = new ArrayList<ParamElement>();
        ArrayList<ParamElement> paramsSuper = new ArrayList<ParamElement>();
        ArrayList<ParamElement> paramsWrite = new ArrayList<ParamElement>();
        if (parentConstructor != null) {
          for (LogtalkParameterModel param : parentConstructor.getParameters()) {
            ParamElement element = new ParamElement(param.getName(), param.getType().toStringWithoutConstant());
            params.add(element);
            paramsSuper.add(element);
          }
        }
        for (LogtalkNamedComponent node : elementsToProcess) {
          ParamElement element = new ParamElement(node.getName(), LogtalkTypeResolver
            .getFieldOrMethodReturnType((AbstractLogtalkNamedComponent)node).toStringWithoutConstant());
          params.add(element);
          paramsWrite.add(element);
        }

        System.out.println(parentConstructor);

        String out = "";
        out += "public function new(";
        boolean first = true;

        PsiElement anchor = null;

        List<LogtalkFieldModel> fields = clazz.getFields();
        for (LogtalkFieldModel field : fields) {
          anchor = field.getBasePsi();
        }

        for (ParamElement param : params) {
          if (!first) {
            out += ",";
          } else {
            first = false;
          }
          out += param.name;
          out += ":";
          out += param.type;
        }

        out += ") {\n";
        if (parentConstructor != null) {
          out += "super(";
          int count = 0;
          for (ParamElement param : paramsSuper) {
            if (count > 0) {
              out += ",";
            }
            out += param.name;
            count++;
          }
          out += ");\n";
        }
        for (ParamElement param : paramsWrite) {
          out += "this." + param.name + " = " + param.name + ";\n";
        }
        out += "}\n\n";

        doAddMethodsForOne(project, out, anchor);
        //super.processElements(project, elementsToProcess);
      }

      @Override
      protected String buildFunctionsText(LogtalkNamedComponent e) {
        return null;
      }
    };
  }

  @Override
  protected String getTitle() {
    return LogtalkBundle.message("fields.to.generate.constructor");
  }

  @Override
  void collectCandidates(LogtalkClass haxeClass, List<LogtalkNamedComponent> candidates) {
    final List<LogtalkNamedComponent> subComponents = LogtalkResolveUtil.getNamedSubComponents(haxeClass);
    final Map<String, LogtalkNamedComponent> componentMap = LogtalkResolveUtil.namedComponentToMap(subComponents);

    for (LogtalkNamedComponent haxeNamedComponent : subComponents) {
      if (!(haxeNamedComponent instanceof LogtalkVarDeclaration)) continue;
      if (haxeNamedComponent.isStatic()) continue;

      //if (!myStrategy.accept(haxeNamedComponent.getName(), componentMap.keySet())) continue;

      candidates.add(haxeNamedComponent);
    }
  }
}

class ParamElement {
  public String name;
  public String type;

  public ParamElement(String name, String type) {
    this.name = name;
    this.type = type;
  }
}