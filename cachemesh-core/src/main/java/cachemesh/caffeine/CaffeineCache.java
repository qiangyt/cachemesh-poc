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
package cachemesh.caffeine;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.local.AbstractLocalCache;
import cachemesh.core.cache.store.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class CaffeineCache<T> extends AbstractLocalCache<T, CaffeineConfig> {

    @Nonnull
    private final Cache<String, Value<T>> caffeineInstance;

    public CaffeineCache(@Nonnull CaffeineProvider provider, @Nonnull CaffeineConfig config,
            @Nullable ShutdownManager shutdownManager, @Nonnull Cache<String, Value<T>> caffeineInstance) {
        super(provider, config, shutdownManager);

        checkNotNull(caffeineInstance);
        this.caffeineInstance = caffeineInstance;
    }

    @Override
    public void onShutdown(@Nonnull ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
        checkNotNull(shutdownLogger);
        this.caffeineInstance.cleanUp();
    }

    @Override
    public void invalidateSingle(@Nonnull String key) {
        checkNotNull(key);
        this.caffeineInstance.invalidate(key);
    }

    @Override
    public void invalidateMultiple(@Nonnull Collection<String> keys) {
        checkNotNull(keys);
        this.caffeineInstance.invalidateAll(keys);
    }

    @Override
    public Value<T> getSingle(@Nonnull String key, @Nullable Function<String, Value<T>> loader) {
        checkNotNull(key);

        if (loader == null) {
            return this.caffeineInstance.getIfPresent(key);
        }
        return this.caffeineInstance.get(key, loader);
    }

    @Override
    @Nonnull
    public Value<T> putSingle(@Nonnull String key, @Nonnull BiFunction<String, Value<T>, Value<T>> mapper) {
        checkNotNull(key);
        checkNotNull(mapper);
        return this.caffeineInstance.asMap().compute(key, mapper);
    }

    @Override
    @Nonnull
    public Map<String, Value<T>> getMultiple(@Nonnull Collection<String> keys) {
        checkNotNull(keys);
        return this.caffeineInstance.getAllPresent(keys);
    }

    // @Override
    // public void putMultiple(Collection<LocalCacheEntry<T>> entries) {
    // this.instance.putAll(LocalCacheEntry.toMap(entries));
    // }

    @Override
    @Nonnull
    public Collection<String> getAllKeys() {
        return this.caffeineInstance.asMap().keySet();
    }

}
