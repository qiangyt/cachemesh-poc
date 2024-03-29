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
package cachemesh.common.config;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public interface Type<T> {

    Class<?> getKlass();

    @Nullable
    Type<?> getElementType();

    @Nullable
    T convert(@Nonnull ConfigContext ctx, @Nullable Object value);

    boolean isEnum();

    boolean isArray();

    boolean isList();

    boolean isMap();

}
