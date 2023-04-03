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
package cachemesh.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.ManagedShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.common.util.LogHelper;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.LocalCacheProvider;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
@ThreadSafe
public class LocalCacheManager implements ManagedShutdownable {

    class Item {
        LocalCache cache;
        LocalCacheConfig config;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Item> items = new ConcurrentHashMap<>();

    private final LocalCacheConfig defaultConfig;

    private final LocalCacheProvider provider;

    private final ShutdownManager shutdownManager;

    private final String name;

    public LocalCacheManager(String name, LocalCacheConfig defaultConfig, LocalCacheProvider provider,
            ShutdownManager shutdownManager) {
        this.name = name;
        this.defaultConfig = defaultConfig;
        this.provider = provider;

        this.shutdownManager = shutdownManager;
        if (shutdownManager != null) {
            shutdownManager.register(this);
        }
    }

    public void addConfig(LocalCacheConfig config) {
        this.items.compute(config.getName(), (name, item) -> {
            if (item != null) {
                throw new InternalException("duplicated configuration %s", name);
            }

            item = new Item();
            item.config = config;
            return item;
        });
    }

    public LocalCacheConfig getConfig(String name) {
        var i = this.items.get(name);
        return (i == null) ? null : i.config;
    }

    public LocalCache get(String name) {
        var i = this.items.get(name);
        return (i == null) ? null : i.cache;
    }

    public LocalCache resolve(String name, Class<?> valueClass) {
        var i = this.items.compute(name, (n, item) -> {
            if (item == null) {
                item = new Item();
                item.config = this.defaultConfig.buildAnother(name, valueClass);
            }

            if (item.cache == null) {
                item.cache = getProvider().create(item.config);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("cache not found, so create it: {}", LogHelper.kv("config", item.config));
                }
            }

            return item;
        });
        return i.cache;
    }

    @Override
    public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();

        // r.put("caches", HasName.toMaps(this.caches));
        r.put("defaultConfig", this.defaultConfig.toMap());

        return r;
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
        var copy = new ArrayList<Item>(this.items.values());
        for (var item : copy) {
            var c = item.cache;
            if (c != null) {
                c.shutdown(timeoutSeconds);
            }
        }
    }

}
