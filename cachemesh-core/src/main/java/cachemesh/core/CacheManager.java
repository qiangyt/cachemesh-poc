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
import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.err.BadStateException;
import cachemesh.common.misc.LogHelper;
import cachemesh.common.shutdown.ManagedShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.config.LocalConfig;
import cachemesh.core.spi.BytesStore;
import cachemesh.core.spi.Cache;
import cachemesh.core.spi.CacheProvider;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ThreadSafe
@Getter
public class CacheManager implements ManagedShutdownable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter(AccessLevel.PROTECTED)
    @Nonnull
    private final Map<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    @Nonnull
    private final LocalConfig config;

    @Nonnull
    private final CacheProvider provider;

    @Nullable
    private final ShutdownManager shutdownManager;

    @Nonnull
    private final String name;

    public CacheManager(@Nonnull String name, @Nonnull LocalConfig config,
            @Nonnull CacheProviderRegistry providerRegistry, @Nullable ShutdownManager shutdownManager) {

        requireNonNull(name);
        requireNonNull(config);
        requireNonNull(providerRegistry);

        this.name = name;
        this.config = config;
        this.provider = providerRegistry.get(config.getKind());

        initLocalCaches();

        this.shutdownManager = shutdownManager;
        if (shutdownManager != null) {
            shutdownManager.register(this);
        }
    }

    protected void initLocalCaches() {
        this.config.getCaches().forEach(cfg -> {
            var cache = this.provider.createCache(cfg);
            this.caches.put(cfg.getName(), cache);
        });
    }

    @Nullable
    public Cache get(@Nonnull String name) {
        requireNonNull(name);
        return doGet(name);
    }

    @Nullable
    protected Cache doGet(@Nonnull String name) {
        return getCaches().get(name);
    }

    @Nullable
    public BytesStore getBytesStore(@Nonnull String name) {
        requireNonNull(name);
        var c = doGet(name);
        return (c == null) ? null : c.getBytesStore();
    }

    @Nonnull
    public Cache resolve(@Nonnull String name) {
        requireNonNull(name);
        return doResolve(name);
    }

    @Nonnull
    protected Cache doResolve(@Nonnull String name) {
        return getCaches().compute(name, (n, cache) -> {
            if (cache == null) {
                cache = getProvider().createDefaultCache(name);

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("cache not found, so create it: {}", LogHelper.kv("config", cache.getConfig()));
                }
            }

            return cache;
        });
    }

    @Nonnull
    public BytesStore resolveBytesStore(@Nonnull String name) {
        requireNonNull(name);
        var c = doResolve(name);
        return c.getBytesStore();
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
        var copy = new ArrayList<Cache>(getCaches().values());
        for (var cache : copy) {
            if (cache != null) {
                cache.shutdown(timeoutSeconds);
            }
        }
    }

}