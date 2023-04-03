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
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class ListAccessor<T> extends Accessor<List<T>> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(Iterable.class));

    private final Accessor<T> elementProperty;

    private final List<T> defaultValue;

    public ListAccessor(Class<?> ownerClass, String name, Accessor<T> elementProperty, List<T> defaultValue) {
        super(ownerClass, name);
        this.elementProperty = elementProperty;
        this.defaultValue = Collections.unmodifiableList(defaultValue);
    }

    @Override
    public List<T> defaultValue() {
        return this.defaultValue;
    }

    @Override
    public List<T> copy(String hint, List<T> value) {
        return new ArrayList<>(value);
    }

    @Override
    public Class<?> propertyClass() {
        return List.class;
    }

    @Override
    public List<T> createEmptyValue() {
        return new ArrayList<T>();
    }

    @Override
    public Collection<Class<?>> convertableClasses() {
        return CONVERTABLE_CLASSES;
    }

    @Override
    public List<T> supply(String hint, Object value) {
        return doConvert(hint, value);
    }

    @Override
    public List<T> doConvert(String hint, Object value) {
        var r = new ArrayList<T>();

        var eltProp = getElementProperty();
        int i = 0;

        for (var childObj : (Iterable<?>) value) {
            var childHint = String.format("%s.%s[%d]", hint, propertyName(), i);
            var child = eltProp.convert(childHint, childObj);
            r.add(child);
            i++;
        }

        return r;
    }

}
