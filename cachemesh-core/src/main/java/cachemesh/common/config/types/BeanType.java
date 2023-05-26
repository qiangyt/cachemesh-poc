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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static java.util.Objects.requireNonNull;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Property;
import cachemesh.common.config.suppport.AbstractType;
import cachemesh.common.config.suppport.ConfigHelper;
import cachemesh.common.err.BadValueException;
import lombok.Getter;

@Getter
public abstract class BeanType<T> extends AbstractType<T> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Map.class);

    private final Class<T> klass;

    public BeanType(@Nonnull Class<T> klass) {
        this.klass = requireNonNull(klass);
    }

    @Override
    public Class<?> getKlass() {
        return this.klass;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Nullable
    public abstract Object extractKind(@Nonnull ConfigContext ctx, @Nonnull Map<String, Object> propValues);

    @Nonnull
    public abstract T newInstance(@Nonnull ConfigContext ctx, @Nullable Object kind);

    @SuppressWarnings("unchecked")
    public void populate(@Nonnull ConfigContext ctx, @Nonnull T bean, @Nullable Object kind,
            @Nonnull Map<String, Object> propValues) {
        requireNonNull(ctx);
        requireNonNull(bean);
        requireNonNull(propValues);

        var props = getProperties(ctx, kind);

        for (var entry : propValues.entrySet()) {
            var propName = entry.getKey();
            var propCtx = ctx.createChild(propName);

            var p = (Property<T, Object>) props.get(propName);
            if (p == null) {
                throw new BadValueException("unexpected prop: %s", propCtx.getPath());
            }

            var unconvertedValue = entry.getValue();
            var convertedValue = p.getType().convert(propCtx, unconvertedValue);
            p.set(bean, convertedValue);
        }
    }

    @Nonnull
    public abstract Map<String, Property<?, ?>> getProperties(@Nonnull ConfigContext ctx, @Nullable Object kind);

    @Override
    @SuppressWarnings("unchecked")
    protected T doConvert(ConfigContext ctx, Object value) {
        var propValues = (Map<String, Object>) value;

        var kind = extractKind(ctx, propValues);
        T bean = newInstance(ctx, kind);
        populate(ctx, bean, kind, propValues);

        return bean;
    }

}
