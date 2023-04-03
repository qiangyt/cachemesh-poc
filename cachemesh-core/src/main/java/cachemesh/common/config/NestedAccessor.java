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

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cachemesh.common.util.Reflect;
import lombok.Getter;

@Getter
public class NestedAccessor<T extends SomeConfig> extends Accessor<T> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(Map.class));

    private final Constructor<T> constructor;

    private final Class<T> propertyClass;

    private final T defaultValue;

    public NestedAccessor(Class<?> ownerClass, String name, Class<T> propertyClass, T defaultValue) {
        super(ownerClass, name);
        this.constructor = Reflect.noArgsConstructor(propertyClass);
        this.propertyClass = propertyClass;
        this.defaultValue = defaultValue;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }

    @Override
    public T createEmptyValue() {
        return Reflect.newInstance(getConstructor());
    }

    @Override
    public Class<?> propertyClass() {
        return this.propertyClass;
    }

    @Override
    public Collection<Class<?>> convertableClasses() {
        return CONVERTABLE_CLASSES;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T doConvert(String hint, Object value) {
        var map = (Map<String, Object>) value;
        T r = createEmptyValue();
        r.withMap(hint, map);
        return r;
    }

}
