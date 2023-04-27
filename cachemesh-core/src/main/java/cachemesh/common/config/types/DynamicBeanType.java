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
package cachemesh.common.config.types;

import java.util.Collections;
import java.util.Map;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Property;
import cachemesh.common.config.TypeRegistry;
import lombok.Getter;

@Getter
public abstract class DynamicBeanType<T> extends BeanType<T> {

    private final TypeRegistry typeRegistry;

    private Map<Object, BeanType<? extends T>> mapping;

    public DynamicBeanType(TypeRegistry typeRegistry, Class<T> klass) {
        super(klass);
        this.typeRegistry = typeRegistry;
    }

    Map<Object, BeanType<? extends T>> getMapping() {
        if (this.mapping == null) {
            var m = createMapping(typeRegistry);
            this.mapping = Collections.unmodifiableMap(m);
        }
        return this.mapping;
    }

    @Override
    public abstract Object extractKind(ConfigContext ctx, Map<String, Object> propValues);

    public abstract Map<String, BeanType<? extends T>> createMapping(TypeRegistry typeRegistry);

    @Override
    public T newInstance(ConfigContext ctx, Object kind) {
        var type = determineConcreteType(ctx, kind);
        return type.newInstance(ctx, kind);
    }

    public BeanType<? extends T> determineConcreteType(ConfigContext ctx, Object kind) {
        return getMapping().get(kind);
    }

    @Override
    public Map<String, Property<?, ?>> getProperties(ConfigContext ctx, Object kind) {
        var type = determineConcreteType(ctx, kind);
        return type.getProperties(ctx, kind);
    }

}
