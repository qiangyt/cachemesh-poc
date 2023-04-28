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

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.cache.bean.Value;
import cachemesh.core.config.LocalCacheConfig;

import javax.annotation.Nonnull;

public interface LocalCache extends Shutdownable {

    @Nonnull
    LocalCacheConfig getConfig();

    void invalidateSingle(@Nonnull String key);

    void invalidateMultiple(@Nonnull Collection<String> keys);

    @Nonnull
    Value getSingle(@Nonnull String key);

    @Nonnull
    Map<String, Value> getMultiple(@Nonnull Collection<String> keys);

    @Nonnull
    Value putSingle(@Nonnull String key, @Nonnull BiFunction<String, Value, Value> mapper);

    // void putMultiple(Collection<LocalCacheEntry<T>> entries);

    @Nonnull
    Collection<String> getAllKeys();

}
