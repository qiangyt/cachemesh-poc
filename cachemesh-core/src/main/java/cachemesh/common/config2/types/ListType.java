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

import java.util.ArrayList;
import java.util.List;

import cachemesh.common.config2.ConfigHelper;
import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.Path;
import cachemesh.common.config2.Type;
import cachemesh.common.config2.TypeRegistry;
import lombok.Getter;

@Getter
public class ListType<T> extends ContainerType<List<T>, T> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Iterable.class);

    public ListType(Type<T> elementType) {
        super(List.class, elementType);
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> doConvert(MapContext ctx, Path path, Object parent, Object value) {
        var eltType = elementType();

        var r = new ArrayList<T>();
        int i = 0;

        for (var childObj : (Iterable<?>) value) {
            var childPath = Path.of(path, String.format("%d", i));
            var child = eltType.convert(ctx, childPath, r, childObj);
            r.add((T) child);
            i++;
        }

        return r;
    }

    public static <E> ListType<E> of(TypeRegistry registry, Type<E> elementType) {
        return new ListType<>(elementType);
    }

}
