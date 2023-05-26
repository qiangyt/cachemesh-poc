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

import java.lang.reflect.Constructor;
import java.util.Map;
import static java.util.Objects.requireNonNull;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Property;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.misc.Reflect;
import lombok.Getter;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

public class ReflectBeanType<T> extends BeanType<T> {

    @Nonnull
    private final Map<String, Property<?, ?>> properties;

    @Getter
    @Nonnull
    private final Constructor<T> ctor;

    public ReflectBeanType(@Nonnull Class<T> klass, @Nonnull Constructor<T> ctor,
            @Nonnull Map<String, Property<T, ?>> properties) {
        super(klass);

        this.ctor = requireNonNull(ctor);
        this.properties = ImmutableMap.copyOf(requireNonNull(properties));
    }

    @Override
    @Nonnull
    public T newInstance(@Nonnull ConfigContext ctx, @Nullable Object kind) {
        requireNonNull(ctx);

        return Reflect.newInstance(getCtor());
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> BeanType<T> of(@Nonnull TypeRegistry typeRegistry, @Nonnull Class<T> klass) {
        requireNonNull(typeRegistry);
        requireNonNull(klass);

        return (BeanType<T>) typeRegistry.resolve(klass, k -> {
            var props = ReflectProperty.of(typeRegistry, klass);
            var ctor = Reflect.defaultConstructor(klass);

            return new ReflectBeanType<T>(klass, ctor, props);
        });
    }

    @Override
    @Nonnull
    public Map<String, Property<?, ?>> getProperties(@Nonnull ConfigContext ctx, @Nullable Object kind) {
        return this.properties;
    }

    @Override
    @Nullable
    public Object extractKind(@Nonnull ConfigContext ctx, @Nonnull Map<String, Object> propValues) {
        return null;
    }

}
