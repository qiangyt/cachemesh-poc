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

import java.lang.reflect.Array;
import java.util.Collection;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Type;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.suppport.ConfigHelper;

public class ArrayType<T> extends ContainerType<T[], T> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Collection.class);

    @SuppressWarnings("unchecked")
    public ArrayType(TypeRegistry registry, Class<T[]> klass) {
        super(klass, (Type<T>) registry.load(klass.getComponentType()));
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T[] doConvert(ConfigContext ctx, Object value) {
        var listValue = (Collection<?>) value;
        var r = (T[]) Array.newInstance(getElementType().getKlass(), listValue.size());

        int i = 0;
        for (var elementV : listValue) {
            var elementCtx = ctx.createChild(i);
            var element = convertElement(elementCtx, elementV);
            Array.set(r, i, element);
            i++;
        }

        return r;
    }

    public static <E> ArrayType<E> of(TypeRegistry registry, Class<E[]> klass) {
        return new ArrayType<>(registry, klass);
    }

}
