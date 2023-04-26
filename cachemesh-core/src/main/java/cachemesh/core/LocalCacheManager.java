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
package cachemesh.core;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.misc.LogHelper;
import cachemesh.common.shutdown.ManagedShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.config.LocalConfig;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.LocalCacheProvider;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

@ThreadSafe
@Getter
public class LocalCacheManager implements ManagedShutdownable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, LocalCache> caches;

    private final LocalConfig localConfig;

    private final LocalCacheProvider localCacheProvider;

    private final ShutdownManager shutdownManager;

    private final String name;

    public LocalCacheManager(String name, LocalConfig localConfig, LocalCacheProviderRegistry localCacheProviderRegistry,
            ShutdownManager shutdownManager) {
        this.name = name;
        this.localConfig = localConfig;
        this.localCacheProvider = localCacheProviderRegistry.get(localConfig.getKind());

        this.caches = initLocalCaches(this.localCacheProvider, localConfig);

        this.shutdownManager = shutdownManager;
        if (shutdownManager != null) {
            shutdownManager.register(this);
        }
    }

    protected Map<String, LocalCache> initLocalCaches(LocalCacheProvider localCacheProvider, LocalConfig localConfig) {
        var r = new ConcurrentHashMap<String, LocalCache>();

        localConfig.getCaches().forEach(cfg -> {
            var cache = localCacheProvider.createCache(cfg);
            r.put(cfg.getName(), cache);
        });

        return r;
    }

    public LocalCache get(String name) {
        return getCaches().get(name);
    }

    public LocalCache resolve(String name, Class<?> valueClass) {
        return getCaches().compute(name, (n, cache) -> {
            if (cache == null) {
                cache = getLocalCacheProvider().createDefaultCache(name, valueClass);
                
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("cache not found, so create it: {}", LogHelper.kv("config", cache.getConfig()));
                }
            }

            return cache;
        });
    }

    @Override
    public void shutdown(int timeoutSeconds) throws InterruptedException {
        if (isShutdownNeeded() == false) {
            throw new IllegalStateException(getName() + " doesn't need shutdown");
        }

        var sd = getShutdownManager();
        if (sd != null) {
            sd.shutdown(this, timeoutSeconds);
        } else {
            onShutdown(createShutdownLogger(), timeoutSeconds);
        }
    }

    public ShutdownLogger createShutdownLogger() {
        return new ShutdownLogger(getLogger());
    }

    @Override
    public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
        var copy = new ArrayList<LocalCache>(getCaches().values());
        for (var cache : copy) {
            if (cache != null) {
                cache.shutdown(timeoutSeconds);
            }
        }
    }

}
