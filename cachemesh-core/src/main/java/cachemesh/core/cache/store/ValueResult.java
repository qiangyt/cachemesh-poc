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
package cachemesh.core.cache.store;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Getter;

@Getter
public class ValueResult<T> {

    @Nonnull
    private final ValueStatus status;

    @Nullable // null only if status is NO_CHANGE
    private final Value<T> value;

    public ValueResult(@Nonnull ValueStatus status, @Nullable Value<T> value) {
        checkNotNull(status);

        this.status = status;
        this.value = value;
    }

    public static final ValueResult<?> NO_CHANGE = new ValueResult<>(ValueStatus.NO_CHANGE, null);

}
