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

import java.util.ArrayList;
import java.util.List;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Type;
import cachemesh.common.config.suppport.ConfigHelper;
import lombok.Getter;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class ListType<T> extends ContainerType<List<T>, T> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Iterable.class);

    public ListType(@Nonnull Type<T> elementType) {
        super(List.class, elementType);
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    protected List<T> doConvert(ConfigContext ctx, Object value) {
        var r = new ArrayList<T>();
        int i = 0;

        for (var elementV : (Iterable<?>) value) {
            var elementCtx = ctx.createChild(i);
            var element = convertElement(elementCtx, elementV);
            r.add(element);
            i++;
        }

        return r;
    }

    @Nonnull
    public static <E> ListType<E> of(@Nonnull Type<E> elementType) {
        checkNotNull(elementType);

        return new ListType<>(elementType);
    }

}
