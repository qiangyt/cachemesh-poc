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
package cachemesh.common.config2.types;

import java.lang.reflect.Array;
import java.util.Collection;

import cachemesh.common.config2.ConfigHelper;
import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.Path;
import cachemesh.common.config2.Type;
import cachemesh.common.config2.TypeRegistry;

public class ArrayType<T> extends ContainerType<T[], T> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Collection.class);

    @SuppressWarnings("unchecked")
    protected ArrayType(TypeRegistry registry, Class<T[]> klass) {
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
    public T[] doConvert(MapContext ctx, Path path, Object parent, Object value) {
        var eltType = elementType();
        var listValue = (Collection<Object>) value;
        var r = (T[]) Array.newInstance(eltType.klass(), listValue.size());

        int i = 0;
        for (var childObj : listValue) {
            var childPath = Path.of(path, String.format("%d", i));
            var child = eltType.convert(ctx, childPath, r, childObj);
            Array.set(r, i, child);
            i++;
        }

        return r;
    }

}
