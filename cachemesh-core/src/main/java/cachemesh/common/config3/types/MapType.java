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

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.config3.Path;
import cachemesh.common.config3.Type;
import lombok.Getter;

@Getter
public class MapType<T> extends ContainerType<Map<String, T>, T> {

    public MapType(Type<T> elementType) {
        super(Map.class, elementType);
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return null;
    }

    @Override
    protected Map<String, T> doConvert(Path path, Object value) {
        var r = new HashMap<String, T>();

        for (var childEntry : ((Map<?, ?>) value).entrySet()) {
            var childK = (String) childEntry.getKey();
            var childV = childEntry.getValue();
            var childP = Path.of(path, String.format("%d", childK));

            var child = convertElement(childP, childV);
            r.put(childK, child);
        }

        return r;
    }

    public static <E> MapType<E> of(Type<E> elementType) {
        return new MapType<>(elementType);
    }

}
