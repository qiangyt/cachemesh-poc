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
package cachemesh.common.config3.reflect;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;

import cachemesh.common.config3.Prop;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.types.BeanType;
import cachemesh.common.misc.Reflect;
import lombok.Getter;

public class ReflectBeanType<T> extends BeanType<T> {

    private final Map<String, Prop<?, ?>> properties;

    @Getter
    private final Constructor<T> ctor;

    public ReflectBeanType(Class<T> klass, Constructor<T> ctor, Map<String, Prop<T, ?>> properties) {
        super(klass);

        this.ctor = ctor;
        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public T newInstance(Object indicator) {
        return Reflect.newInstance(getCtor());
    }

    @SuppressWarnings("unchecked")
    public static <T> BeanType<T> of(TypeRegistry typeRegistry, Class<T> klass) {
        return (BeanType<T>) typeRegistry.resolve(klass, k -> {
            var props = ReflectProp.of(typeRegistry, klass);
            var ctor = Reflect.defaultConstructor(klass);

            return new ReflectBeanType<T>(klass, ctor, props);
        });
    }

    @Override
    public Map<String, Prop<?, ?>> getProperties(Object indicator) {
        return this.properties;
    }

}
