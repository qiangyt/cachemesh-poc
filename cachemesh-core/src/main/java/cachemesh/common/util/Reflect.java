/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cachemesh.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Reflect {

    public static <T> Constructor<T> noArgsConstructor(Class<T> propertyClass) {
        try {
            return propertyClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Constructor<T> ctor, Object... args) {
        try {
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getterName(String name, Class<?> propertyClass) {
        var verb = (Boolean.class.isAssignableFrom(propertyClass) || boolean.class.isAssignableFrom(propertyClass))
            ? "is"
            : "get";
        return verb + StringHelper.capitalize(name);
    }

    public static Method getter(Class<?> ownerClass, String propertyName, Class<?> propertyClass) {
        var methodName = Reflect.getterName(propertyName, propertyClass);
        try {
            Method method = ownerClass.getDeclaredMethod(methodName);
            if (method.getReturnType() != propertyClass) {
                var msg = String.format("expect getter %s returns %s, but got %s", methodName, propertyClass,
                    method.getReturnType());
                throw new RuntimeException(msg);
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static String setterName(String name) {
        return "set" + StringHelper.capitalize(name);
    }

    public static Method setter(Class<?> ownerClass, String propertyName, Class<?> propertyClass) {
        var methodName = Reflect.setterName(propertyName);
        try {
            return ownerClass.getDeclaredMethod(methodName, propertyClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
