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
public abstract class NestedDynamicOp<T extends SomeConfig> extends NestedOp<T> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(Map.class));

    private final Map<Object, NestedOp<T>> factory;

    public NestedDynamicOp(Class<T> propertyClass, Map<Object, NestedOp<T>> factory) {
        super(propertyClass);

        this.factory = factory;
    }

    public abstract Object extractKey(String hint, Map<String, Object> map);

    @Override
    public T newValue(String hint, Map<String, Object> map) {
        var key = extractKey(hint, map);
        var targetOp = getFactory().get(key);
        return targetOp.newValue(hint, map);
    }

}
