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

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.bean.Value;
import cachemesh.core.cache.local.LocalCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class CaffeineCache extends AbstractShutdownable implements LocalCache {

    @Nonnull
    private final CaffeineConfig config;

    @Nonnull
    private final Cache<String, Value> instance;

    public CaffeineCache(@Nonnull CaffeineConfig config, @Nonnull Cache<String, Value> instance,
            @Nullable ShutdownManager shutdownManager) {
        super(config.getName(), shutdownManager);

        checkNotNull(instance);

        this.config = config;
        this.instance = instance;
    }

    @Override
    public String toString() {
        return getConfig().toString();
    }

    @Override
    public void onShutdown(@Nonnull ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
        checkNotNull(shutdownLogger);
        this.instance.cleanUp();
    }

    @Override
    public void invalidateSingle(@Nonnull String key) {
        checkNotNull(key);
        this.instance.invalidate(key);
    }

    @Override
    public void invalidateMultiple(@Nonnull Collection<String> keys) {
        checkNotNull(keys);
        this.instance.invalidateAll(keys);
    }

    @Override
    public Value getSingle(@Nonnull String key) {
        checkNotNull(key);
        return this.instance.getIfPresent(key);
    }

    @Override
    @Nonnull
    public Value putSingle(@Nonnull String key, @Nonnull BiFunction<String, Value, Value> mapper) {
        checkNotNull(key);
        checkNotNull(mapper);
        return this.instance.asMap().compute(key, mapper);
    }

    @Override
    @Nonnull
    public Map<String, Value> getMultiple(@Nonnull Collection<String> keys) {
        checkNotNull(keys);
        return this.instance.getAllPresent(keys);
    }

    // @Override
    // public void putMultiple(Collection<LocalCacheEntry<T>> entries) {
    // this.instance.putAll(LocalCacheEntry.toMap(entries));
    // }

    @Override
    @Nonnull
    public Collection<String> getAllKeys() {
        return this.instance.asMap().keySet();
    }

}
