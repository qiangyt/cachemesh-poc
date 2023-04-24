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
package cachemesh.common.config3.types;

import java.util.Collections;
import java.util.Map;

import cachemesh.common.config3.Prop;
import cachemesh.common.config3.TypeRegistry;
import lombok.Getter;

public abstract class MappedBeanType<T> extends BeanType<T> {

    @Getter
    private final Map<String, BeanType<? extends T>> mapping;

    public MappedBeanType(TypeRegistry typeRegistry, Class<T> klass) {
        super(klass);

        var mapping = createMapping(typeRegistry);
        this.mapping = Collections.unmodifiableMap(mapping);
    }

    public abstract Map<String, BeanType<? extends T>> createMapping(TypeRegistry typeRegistry);

    @Override
    public T newInstance(Object indicator) {
        var type = determineConcreteType(indicator);
        return type.newInstance(indicator);
    }

    public BeanType<? extends T> determineConcreteType(Object indicator) {
        var kind = (String) indicator;
        return getMapping().get(kind);
    }

    @Override
    public Map<String, Prop<?, ?>> getProperties(Object indicator) {
        var type = determineConcreteType(indicator);
        return type.getProperties(indicator);
    }

}
