/*
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cachemesh.common.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Reflect {

    public static <T> Constructor<T> noArgsConstructor(Class<T> propertyClass) {
        Constructor<T> r;
        try {
            r = propertyClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        r.setAccessible(true);
        return r;
    }

    public static <T> T newInstance(Constructor<T> ctor, Object... args) {
        try {
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String getterName(String name, Class<?> propertyClass) {
        var verb = (Boolean.class.isAssignableFrom(propertyClass) || boolean.class.isAssignableFrom(propertyClass))
                ? "is" : "get";
        return verb + StringHelper.capitalize(name);
    }

    public static Method getter(Class<?> ownerClass, String propertyName, Class<?> propertyClass) {
        var methodName = Reflect.getterName(propertyName, propertyClass);

        Method r;
        try {
            r = ownerClass.getDeclaredMethod(methodName);
            if (r.getReturnType() != propertyClass) {
                var msg = String.format("expect getter %s returns %s, but got %s", methodName, propertyClass,
                        r.getReturnType());
                throw new IllegalArgumentException(msg);
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("no getter method for property " + propertyName);
        }

        r.setAccessible(true);
        return r;
    }

    public static String setterName(String name) {
        return "set" + StringHelper.capitalize(name);
    }

    public static Method setter(Class<?> ownerClass, String propertyName, Class<?> propertyClass) {
        var methodName = Reflect.setterName(propertyName);

        Method r;
        try {
            r = ownerClass.getDeclaredMethod(methodName, propertyClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("no setter method for property " + propertyName, e);
        }

        r.setAccessible(true);
        return r;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Method getter, Object object) {
        try {
            return (T) getter.invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(Method setter, Object object, Object value) {
        try {
            setter.invoke(object, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
