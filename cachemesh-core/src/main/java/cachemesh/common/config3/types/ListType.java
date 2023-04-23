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

import java.util.ArrayList;
import java.util.List;

import cachemesh.common.config3.Path;
import cachemesh.common.config3.ConfigHelper;
import cachemesh.common.config3.Type;
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
    protected List<T> doConvert(Path path, Object value) {
        var r = new ArrayList<T>();
        int i = 0;

        for (var childV : (Iterable<?>) value) {
            var childP = Path.of(path, String.format("%d", i));

            var child = convertElement(childP, childV);
            r.add(child);
            i++;
        }

        return r;
    }

    public static <E> ListType<E> of(Type<E> elementType) {
        return new ListType<>(elementType);
    }

}
