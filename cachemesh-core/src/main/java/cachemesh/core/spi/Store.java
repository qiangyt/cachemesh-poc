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
package cachemesh.core.spi;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.core.bean.Value;
import cachemesh.core.bean.ValueResult;

public interface Store<T> {

    @Nullable ValueResult<T> getSingle(@Nonnull String key, long version, @Nullable Function<String, Value<T>> loader);

    Value<T> putSingle(@Nonnull String key, @Nullable T value);

    void removeSingle(@Nonnull String key);

}