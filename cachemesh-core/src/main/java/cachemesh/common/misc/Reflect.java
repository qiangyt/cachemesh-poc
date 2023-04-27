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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cachemesh.common.err.BadStateException;

public class Reflect {

    public static String propertyName(Field field, String propertyName) {
        if (StringHelper.isBlank(propertyName)) {
            propertyName = field.getName().toLowerCase();
        }
        return propertyName.trim();
    }

    public static <T> Constructor<T> defaultConstructor(Class<T> propertyClass) {
        Constructor<T> r;
        try {
            r = propertyClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BadStateException(e);
        }
        r.setAccessible(true);
        return r;
    }

    public static <T> T newInstance(Constructor<T> ctor) {
        try {
            return ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    public static <T> T newInstance(Constructor<T> ctor, Object... args) {
        try {
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Method getter, Object object) {
        try {
            return (T) getter.invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    public static void set(Method setter, Object object, Object value) {
        try {
            setter.invoke(object, value);
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    public static Method method(Class<?> klass, String name, Class<?>... paramTypes) {
        try {
            return klass.getMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method setter(Class<?> beanClass, String propName, Class<?> propClass, String setterName) {
        if (StringHelper.isBlank(setterName)) {
            setterName = "set" + StringHelper.capitalize(propName);
        }

        Method r = method(beanClass, setterName, propClass);
        if (r == null) {
            var msg = String.format("failed to find setter method '%s' for property '%s.%s'", setterName, beanClass,
                    propName);
            throw new BadStateException(msg);
        }
        r.setAccessible(true);

        return r;
    }

    public static Method getter(Class<?> beanClass, String propName, Class<?> propClass, String getterName) {
        if (StringHelper.isBlank(getterName)) {
            String propCapName = StringHelper.capitalize(propName);

            Method r;
            if (propClass == boolean.class || propClass == Boolean.class) {
                getterName = "is" + propCapName;
                r = _getter(beanClass, propName, propClass, getterName);
                if (r != null) {
                    return r;
                }
            }

            getterName = "get" + propCapName;
        }

        Method r = _getter(beanClass, propName, propClass, getterName);
        if (r == null) {
            var msg = String.format("failed to find getter '%s' for property '%s.%s'", getterName, beanClass, propName);
            throw new BadStateException(msg);
        }
        return r;
    }

    private static Method _getter(Class<?> beanClass, String propName, Class<?> propClass, String getterName) {
        Method r = method(beanClass, getterName);
        if (r == null) {
            return null;
        }
        if (r.getReturnType() != propClass) {
            var msg = String.format("expect getter '%s' returns %s for property '%s.%s', but got %s", getterName,
                    beanClass, propName, r.getReturnType());
            throw new BadStateException(msg);
        }
        r.setAccessible(true);

        return r;
    }

}
