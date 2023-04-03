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

import java.util.ArrayList;
import java.util.Collection;

import cachemesh.common.util.StringHelper;

public abstract class Operator<T> {

    public abstract Class<?> propertyClass();

    public Collection<Class<?>> convertableClasses() {
        return null;
    }

    public T createZeroValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public T supply(String hint, Object value) {
        return (T) value;
    }

    public T copy(String hint, T value) {
        return value;
    }

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
