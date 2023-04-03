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
package cachemesh.common.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import cachemesh.common.util.Reflect;
import cachemesh.common.util.StringHelper;

public abstract class Accessor<T> {

    private final String propertyName;

    private final Method setter;

    private final Method getter;

    public Accessor(Class<?> configClass, String propertyName) {
        this.propertyName = propertyName;

        var propClass = propertyClass();
        this.setter = Reflect.setter(configClass, propertyName, propClass);
        this.getter = Reflect.getter(configClass, propertyName, propClass);
    }

    public String propertyName() {
        return this.propertyName;
    }

    public Method setter() {
        return this.setter;
    }

    public Method getter() {
        return this.getter;
    }

    public abstract Class<?> propertyClass();

    public Collection<Class<?>> convertableClasses() {
        return null;
    }

    public abstract T defaultValue();

    public abstract T createEmptyValue();

    public T convert(String hint, Object value) {
        if (value == null) {
            return null;
        }
        if (propertyClass().isAssignableFrom(value.getClass())) {
            return supply(hint, value);
        }

        if (isConvertable(value) == false) {
            throw invalidValueClassError(hint, value);
        }

        return doConvert(hint, value);
    }

    @SuppressWarnings("unchecked")
    public T supply(String hint, Object value) {
        return (T) value;
    }

    public T copy(String hint, T value) {
        return value;
    }

    public boolean isConvertable(Object value) {
        var classes = convertableClasses();
        if (classes == null || classes.isEmpty()) {
            return false;
        }

        var actual = value.getClass();
        for (var expected : classes) {
            if (expected.isAssignableFrom(actual)) {
                return true;
            }
        }

        return false;
    }

    public T doConvert(String hint, Object value) {
        throw new UnsupportedOperationException("To be implemented");
    }

    @SuppressWarnings("unchecked")
    public T get(Object object) {
        try {
            return (T) getter().invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String hint, Object object, Object value) {
        var v = convert(hint, value);

        try {
            setter().invoke(object, v);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public IllegalArgumentException invalidValueClassError(String hint, Object value) {
        var classes = new ArrayList<Class<?>>();
        classes.add(propertyClass());

        var others = convertableClasses();
        if (others != null && others.isEmpty() == false) {
            classes.addAll(others);
        }

        var msg = String.format("%s: expect be %s, but got %s", hint, StringHelper.join("/", classes),
            value.getClass());
        return new IllegalArgumentException(msg);
    }

}
