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
package cachemesh.core.cache.transport;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.cache.bean.LocalValue;
import cachemesh.core.config.LocalCacheConfig;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ThreadSafe
public interface LocalCache<T> extends Shutdownable {

    void open(int timeoutSeconds) throws InterruptedException;

    @Nonnull
    LocalCacheConfig getConfig();
    
    void invalidateSingle(@Nonnull String key);

    void invalidateMultiple(@Nonnull Collection<String> keys);

    @Nullable
    LocalValue<T> getSingle(@Nonnull String key);

    @Nonnull
    Map<String, LocalValue<T>> getMultiple(@Nonnull Collection<String> keys);

    LocalValue<T> putSingle(@Nonnull String key, @Nonnull BiFunction<String, LocalValue<T>, LocalValue<T>> mapper);

    // void putMultiple(Collection<LocalCacheEntry<T>> entries);

    @Nonnull
    Collection<String> getAllKeys();

}
