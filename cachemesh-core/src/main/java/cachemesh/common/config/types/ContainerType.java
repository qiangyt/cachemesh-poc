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

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Type;
import cachemesh.common.config.suppport.AbstractType;
import lombok.Getter;
import static com.google.common.base.Preconditions.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
public abstract class ContainerType<T, E> extends AbstractType<T> {

    @Nonnull
    private final Type<E> elementType;

    @Nonnull
    private final Class<?> klass;

    protected ContainerType(@Nonnull Class<?> klass, @Nonnull Type<E> elementType) {
        this.klass = checkNotNull(klass);
        this.elementType = checkNotNull(elementType);
    }

    @Override
    @Nullable
    public T convert(@Nonnull ConfigContext ctx, @Nullable Object value) {
        checkNotNull(ctx);

        if (value == null) {
            return null;
        }

        if (isConvertable(value) == false) {
            throw badValueClassError(ctx, value.getClass());
        }

        return doConvert(ctx, value);
    }

    @Nullable
    protected E convertElement(@Nonnull ConfigContext elementCtx, @Nullable Object elementValue) {
        return getElementType().convert(elementCtx, elementValue);
    }

}
