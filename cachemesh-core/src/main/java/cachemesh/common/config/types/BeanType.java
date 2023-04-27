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

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Property;
import cachemesh.common.config.suppport.AbstractType;
import cachemesh.common.config.suppport.ConfigHelper;
import lombok.Getter;

@Getter
public abstract class BeanType<T> extends AbstractType<T> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Map.class);

    private final Class<T> klass;

    public BeanType(Class<T> klass) {
        this.klass = klass;
    }

    @Override
    public Class<?> getKlass() {
        return this.klass;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    public abstract Object extractKind(ConfigContext ctx, Map<String, Object> propValues);

    public abstract T newInstance(ConfigContext ctx, Object kind);

    @SuppressWarnings("unchecked")
    public void populate(ConfigContext ctx, T bean, Object kind, Map<String, Object> propValues) {
        var props = getProperties(ctx, kind);

        for (var entry : propValues.entrySet()) {
            var propName = entry.getKey();
            var propCtx = ctx.createChild(propName);

            var p = (Property<T, Object>) props.get(propName);
            if (p == null) {
                var msg = String.format("unexpected prop: %s", propCtx.getPath());
                throw new IllegalArgumentException(msg);
            }

            var unconvertedValue = entry.getValue();
            var convertedValue = p.getType().convert(propCtx, unconvertedValue);
            p.set(bean, convertedValue);
        }
    }

    public abstract Map<String, Property<?, ?>> getProperties(ConfigContext ctx, Object kind);

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
