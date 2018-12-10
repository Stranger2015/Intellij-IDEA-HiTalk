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
package logtalk.lang;

import logtalk.root.Array;

public class LogtalkObject implements ILogtalkObject {
  @Override
  public boolean __lgt_deleteField(String field) {
    return false;
  }

  @Override
  public Object __lgt_lookupField(String field, boolean throwErrors, boolean isCheck) {
    return null;
  }

  @Override
  public double __lgt_lookupField_f(String field, boolean throwErrors) {
    return 0;
  }

  @Override
  public Object __lgt_lookupSetField(String field, Object value) {
    return null;
  }

  @Override
  public double __lgt_lookupSetField_f(String field, double value) {
    return 0;
  }

  @Override
  public double __lgt_setField_f(String field, double value, boolean handleProperties) {
    return 0;
  }

  @Override
  public Object __lgt_setField(String field, Object value, boolean handleProperties) {
    return null;
  }

  @Override
  public Object __lgt_getField(String field, boolean throwErrors, boolean isCheck, boolean handleProperties) {
    return null;
  }

  @Override
  public double __lgt_getField_f(String field, boolean throwErrors, boolean handleProperties) {
    return 0;
  }

  @Override
  public Object __lgt_invokeField(String field, Array dynargs) {
    return null;
  }

  @Override
  public void __lgt_getFields(Array<String> baseArr) {

  }
}
