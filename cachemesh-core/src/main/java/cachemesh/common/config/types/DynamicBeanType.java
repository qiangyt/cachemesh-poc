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

import java.util.Map;
import static java.util.Objects.requireNonNull;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Property;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.err.BadStateException;
import lombok.Getter;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;

@Getter
public abstract class DynamicBeanType<T> extends BeanType<T> {

    @Nonnull
    private final TypeRegistry typeRegistry;

    @Nonnull
    private Map<Object, BeanType<? extends T>> mapping;

    public DynamicBeanType(@Nonnull TypeRegistry typeRegistry, @Nonnull Class<T> klass) {
        super(klass);
        this.typeRegistry = requireNonNull(typeRegistry);
    }

    @Nonnull
    Map<Object, BeanType<? extends T>> getMapping() {
        if (this.mapping == null) {
            var m = createMapping(typeRegistry);
            this.mapping = ImmutableMap.copyOf(m);
        }
        return this.mapping;
    }

    @Override
    @Nonnull
    public abstract Object extractKind(@Nonnull ConfigContext ctx, @Nonnull Map<String, Object> propValues);

    @Nonnull
    public abstract Map<String, BeanType<? extends T>> createMapping(@Nonnull TypeRegistry typeRegistry);

    @Override
    @Nonnull
    public T newInstance(@Nonnull ConfigContext ctx, @Nonnull Object kind) {
        requireNonNull(ctx);
        requireNonNull(kind);

        var type = determineConcreteType(ctx, kind);
        return type.newInstance(ctx, kind);
    }

    @Nonnull
    public BeanType<? extends T> determineConcreteType(@Nonnull ConfigContext ctx, @Nonnull Object kind) {
        requireNonNull(ctx);
        requireNonNull(kind);

        var r = getMapping().get(kind);
        if (r == null) {
            throw new BadStateException("cannot determine concrete %s type for kind=%s", getKlass(), kind);
        }
        return r;
    }

    @Override
    @Nonnull
    public Map<String, Property<?, ?>> getProperties(@Nonnull ConfigContext ctx, @Nonnull Object kind) {
        requireNonNull(ctx);
        requireNonNull(kind);

        var type = determineConcreteType(ctx, kind);
        return type.getProperties(ctx, kind);
    }

}
