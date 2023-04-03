/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import cachemesh.core.config.CaffeineConfig;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.Value;

@Getter
public class CaffeineCache extends AbstractShutdownable implements LocalCache {

    private final CaffeineConfig       config;

    private final Cache<String, Value> instance;

    public CaffeineCache(CaffeineConfig config, Cache<String, Value> instance, ShutdownManager shutdownManager) {
        super(config.getName(), shutdownManager);

        this.config = config;
        this.instance = instance;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> r = super.toMap();
        r.put("config", getConfig().toMap());
        return r;
    }

    @Override
    public String toString() {
        return getConfig().toString();
    }

    @Override
    public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
        this.instance.cleanUp();
    }

    @Override
    public void invalidateSingle(String key) {
        this.instance.invalidate(key);
    }

    @Override
    public void invalidateMultiple(Collection<String> keys) {
        this.instance.invalidateAll(keys);
    }

    @Override
    public Value getSingle(String key) {
        return this.instance.getIfPresent(key);
    }

    @Override
    public Value putSingle(String key, BiFunction<String, Value, Value> mapper) {
        return this.instance.asMap().compute(key, mapper);
    }

    @Override
    public Map<String, Value> getMultiple(Collection<String> keys) {
        return this.instance.getAllPresent(keys);
    }

    // @Override
    // public void putMultiple(Collection<LocalCacheEntry<T>> entries) {
    // this.instance.putAll(LocalCacheEntry.toMap(entries));
    // }

    @Override
    public Collection<String> getAllKeys() {
        return this.instance.asMap().keySet();
    }

}
