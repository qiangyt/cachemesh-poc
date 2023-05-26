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

import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.requireNonNull;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Type;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class MapType<T> extends ContainerType<Map<String, T>, T> {

    public MapType(@Nonnull Type<T> elementType) {
        super(Map.class, elementType);
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return null;
    }

    @Override
    protected Map<String, T> doConvert(ConfigContext ctx, Object value) {
        var r = new HashMap<String, T>();

        for (var elementEntry : ((Map<?, ?>) value).entrySet()) {
            var elementName = (String) elementEntry.getKey();
            var elementCtx = ctx.createChild(elementName);
            var elementV = elementEntry.getValue();
            var element = convertElement(elementCtx, elementV);
            r.put(elementName, element);
        }

        return r;
    }

    @Nonnull
    public static <E> MapType<E> of(@Nonnull Type<E> elementType) {
        requireNonNull(elementType);

        return new MapType<>(elementType);
    }

}
