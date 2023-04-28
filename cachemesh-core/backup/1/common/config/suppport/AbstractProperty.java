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
package cachemesh.common.config.suppport;

import cachemesh.common.config.Property;
import cachemesh.common.config.Type;
import lombok.Getter;
import static com.google.common.base.Preconditions.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
public abstract class AbstractProperty<B, T> implements Property<B, T> {

    @Nonnull
    private final String name;

    @Nonnull
    private final Type<T> type;

    @Nullable
    private final T devault;

    public AbstractProperty(@Nonnull String name, @Nonnull Type<T> type, @Nullable T devault) {
        this.name = checkNotNull(name);
        this.type = checkNotNull(type);
        this.devault = devault;
    }

}
