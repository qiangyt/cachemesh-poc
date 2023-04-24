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

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.Path;

@Getter
public abstract class DynamicBeanType<T> extends BeanType<T> {

    private final TypeRegistry typeRegistry;

    private final Path kindPath;

    private final Map<Object, BeanType<? extends T>> mapping;

    private final Object defaultKind;

    public DynamicBeanType(TypeRegistry typeRegistry, Class<T> klass, Path kindPath, Object defaultKind) {
        super(klass);
        this.typeRegistry = typeRegistry;
        this.kindPath = kindPath;
        this.defaultKind = defaultKind;

        var mapping = createMapping(typeRegistry);
        this.mapping = Collections.unmodifiableMap(mapping);
    }
    
    @Override
    public Object extractKind(ConvertContext ctx, Map<String, Object> propValues) {
        var r = ctx.getValue(getKindPath());
        if (r == null) {
            r = getDefaultKind();
            if (r == null) {
                var msg = String.format("%s is required", getKindPath());
                throw new IllegalStateException(msg);
            }
        }
        return r;
    }

    public abstract Map<String, BeanType<? extends T>> createMapping(TypeRegistry typeRegistry);

    @Override
    public T newInstance(ConvertContext ctx, Object kind) {
        var type = determineConcreteType(ctx, kind);
        return type.newInstance(ctx, kind);
    }

    public BeanType<? extends T> determineConcreteType(ConvertContext ctx, Object kind) {
        return getMapping().get(kind);
    }

    @Override
    public Map<String, Prop<?, ?>> getProperties(ConvertContext ctx, Object kind) {
        var type = determineConcreteType(ctx, kind);
        return type.getProperties(ctx, kind);
    }

}
