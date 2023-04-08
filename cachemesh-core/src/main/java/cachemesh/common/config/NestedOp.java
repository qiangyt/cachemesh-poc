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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public abstract class NestedOp<T extends SomeConfig> implements Operator<T> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(Map.class));

    private final Class<T> propertyClass;

    public NestedOp(Class<T> propertyClass) {
        this.propertyClass = propertyClass;
    }

    public abstract T newValue(String hint, Map<String, Object> map);

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
        T r = newValue(hint, map);
        r.withMap(hint, map);
        return r;
    }

}
