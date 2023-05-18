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
package cachemesh.core.cache.local;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.err.BadStateException;
import cachemesh.common.misc.LogHelper;
import cachemesh.common.shutdown.ManagedShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.spi.LocalCache;
import cachemesh.core.cache.spi.LocalCacheProvider;
import cachemesh.core.config.LocalConfig;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@ThreadSafe
@Getter
public class LocalCacheManager implements ManagedShutdownable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter(AccessLevel.PROTECTED)
    @Nonnull
    private final Map<String, LocalCache<?>> caches;

    @Nonnull
    private final LocalConfig config;

    @Nonnull
    private final LocalCacheProvider provider;

    @Nullable
    private final ShutdownManager shutdownManager;

    @Nonnull
    private final String name;

    public LocalCacheManager(@Nonnull String name, @Nonnull LocalConfig config,
            @Nonnull LocalCacheProviderRegistry providerRegistry, @Nullable ShutdownManager shutdownManager) {

        checkNotNull(name);
        checkNotNull(config);
        checkNotNull(providerRegistry);

        this.name = name;
        this.config = config;
        this.provider = providerRegistry.get(config.getKind());

        this.caches = initLocalCaches(this.provider, config);

        this.shutdownManager = shutdownManager;
        if (shutdownManager != null) {
            shutdownManager.register(this);
        }
    }

    protected Map<String, LocalCache<?>> initLocalCaches(@Nonnull LocalCacheProvider localCacheProvider,
            @Nonnull LocalConfig localConfig) {
        var r = new ConcurrentHashMap<String, LocalCache<?>>();

        localConfig.getCaches().forEach(cfg -> {
            var cache = localCacheProvider.createCache(cfg);
            r.put(cfg.getName(), cache);
        });

        return r;
    }

    @Nullable
    public LocalCache<?> get(@Nonnull String name) {
        checkNotNull(name);
        return getCaches().get(name);
    }

    @Nonnull
    public LocalCache<?> resolve(@Nonnull String name, @Nonnull Class<?> valueClass) {
        checkNotNull(name);
        checkNotNull(valueClass);

        return getCaches().compute(name, (n, cache) -> {
            if (cache == null) {
                cache = getProvider().createDefaultCache(name, valueClass);

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
            throw new BadStateException("%s doesn't need shutdown", getName());
        }

        var sd = getShutdownManager();
        if (sd != null) {
            sd.shutdown(this, timeoutSeconds);
        } else {
            onShutdown(createShutdownLogger(), timeoutSeconds);
        }
    }

    @Nullable
    public ShutdownLogger createShutdownLogger() {
        return new ShutdownLogger(getLogger());
    }

    @Override
    public void onShutdown(@Nonnull ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
        var copy = new ArrayList<LocalCache<?>>(getCaches().values());
        for (var cache : copy) {
            if (cache != null) {
                cache.shutdown(timeoutSeconds);
            }
        }
    }

}
