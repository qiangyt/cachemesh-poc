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
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

import cachemesh.common.err.BadStateException;

public class Reflect {

    @Nonnull
    public static String propertyName(@Nonnull Field field, @Nullable String propertyName) {
        requireNonNull(field);

        if (StringHelper.isBlank(propertyName)) {
            propertyName = field.getName().toLowerCase();
        }
        return propertyName.trim();
    }

    @Nonnull
    public static <T> Constructor<T> defaultConstructor(@Nonnull Class<T> klass) {
        requireNonNull(klass);

        Constructor<T> r;
        try {
            r = klass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BadStateException(e);
        }
        r.setAccessible(true);
        return r;
    }

    @Nonnull
    public static <T> T newInstance(@Nonnull Constructor<T> ctor) {
        requireNonNull(ctor);

        try {
            return ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    @Nonnull
    public static <T> T newInstance(@Nonnull Constructor<T> ctor, @Nonnull Object... args) {
        requireNonNull(ctor);
        requireNonNull(args);

        try {
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T get(@Nonnull Method getter, @Nonnull Object object) {
        requireNonNull(getter);
        requireNonNull(object);

        try {
            return (T) getter.invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    @Nonnull
    public static void set(@Nonnull Method setter, @Nonnull Object object, @Nullable Object value) {
        requireNonNull(setter);
        requireNonNull(object);

        try {
            setter.invoke(object, value);
        } catch (ReflectiveOperationException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public static Method method(@Nonnull Class<?> klass, @Nonnull String name, @Nonnull Class<?>... paramTypes) {
        requireNonNull(klass);
        requireNonNull(name);
        requireNonNull(paramTypes);

        try {
            return klass.getMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nonnull
    public static Method setter(@Nonnull Class<?> beanClass, @Nonnull String propName, @Nonnull Class<?> propClass,
            @Nullable String setterName) {
        requireNonNull(beanClass);
        requireNonNull(propName);
        requireNonNull(propClass);

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

    @Nonnull
    public static Method getter(@Nonnull Class<?> beanClass, @Nonnull String propName, @Nonnull Class<?> propClass,
            @Nullable String getterName) {
        requireNonNull(beanClass);
        requireNonNull(propName);
        requireNonNull(propClass);

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
