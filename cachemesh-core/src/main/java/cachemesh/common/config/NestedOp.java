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
package cachemesh.common.config;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cachemesh.common.util.Reflect;
import lombok.Getter;

@Getter
public class NestedOp<T extends SomeConfig> extends Operator<T> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(Map.class));

    private final Constructor<T> constructor;

    private final Class<T> propertyClass;

    public NestedOp(Class<T> propertyClass) {
        this.constructor = Reflect.noArgsConstructor(propertyClass);
        this.propertyClass = propertyClass;
    }

    @Override
    public T createZeroValue() {
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
        T r = createZeroValue();
        r.withMap(hint, map);
        return r;
    }

}
