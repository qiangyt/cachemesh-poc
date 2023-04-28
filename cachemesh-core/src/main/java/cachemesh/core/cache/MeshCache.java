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
package cachemesh.core.cache;

import org.slf4j.Logger;

import lombok.Getter;
import cachemesh.common.err.BadStateException;
import cachemesh.common.err.ToDoException;
import cachemesh.common.misc.LogHelper;
import cachemesh.core.MeshNetwork;
import cachemesh.core.cache.bean.ValueImpl;
import cachemesh.core.cache.local.LocalCache;
import cachemesh.core.cache.local.LocalCacheManager;
import cachemesh.core.cache.transport.Transport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Getter
public class MeshCache<T> {

    @Nonnull
    private final Logger logger;

    @Nonnull
    private final LocalCache nearCache;

    @Nonnull
    private final MeshNetwork network;

    public MeshCache(@Nonnull String name, @Nonnull LocalCacheManager nearCacheManager, @Nonnull MeshNetwork network) {
        checkNotNull(name);
        checkNotNull(nearCacheManager);
        checkNotNull(network);

        this.nearCache = nearCacheManager.get(name);
        this.network = network;

        this.logger = LogHelper.getLogger(getClass(), name);
    }

    @Nonnull
    public String getName() {
        return this.nearCache.getConfig().getName();
    }

    @Nonnull
    public Transport resolveTransport(@Nonnull String key) {
        checkNotNull(key);

        var n = getNetwork().findNode(key);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("find node for {}: {}", kv("key", key), LogHelper.kv("node", n.getConfig()));
        }

        return n.getTransport();
    }

    @Nullable
    public T getSingle(@Nonnull String key) {
        checkNotNull(key);

        var transport = resolveTransport(key);
        if (transport.isRemote() == false) {
            return transport.getSingleObject(getName(), key);
        }
        return getRemoteSingle(transport, key);
    }

    @Nullable
    protected T getRemoteSingle(@Nonnull Transport transport, @Nonnull String key) {
        checkNotNull(transport);
        checkNotNull(key);

        var near = getNearCache();
        var cfg = near.getConfig();
        var serder = cfg.getSerder().getKind().instance;

        @SuppressWarnings("unchecked")
        var valueClass = (Class<T>) cfg.getValueClass();

        var nearValue = near.getSingle(key);
        long version = (nearValue == null) ? 0 : nearValue.getVersion();

        var r = transport.getSingle(getName(), key, version);

        switch (r.getStatus()) {
        case OK: {
            var valueBytes = r.getValue();
            T valueObj = serder.deserialize(valueBytes, valueClass);
            near.putSingle(key, (k, v) -> new ValueImpl(valueObj, valueBytes, r.getVersion()));
            return valueObj;
        }
        case NO_CHANGE:
            return nearValue.getObject(serder, valueClass);
        case NOT_FOUND: {
            near.invalidateSingle(key);
            return null;
        }
        case REDIRECT: {
            near.invalidateSingle(key);
            throw new ToDoException("TODO");
        }
        default:
            throw new BadStateException("unexpected status: %s", r.getStatus());
        }
    }

    public void putSingle(@Nonnull String key, @Nullable T object) {
        checkNotNull(key);

        var transport = resolveTransport(key);
        if (transport.isRemote() == false) {
            putLocalSingle(transport, key, object);
        } else {
            putRemoteSingle(transport, key, object);
        }
    }

    @SuppressWarnings("unchecked")
    protected void putLocalSingle(@Nonnull Transport transport, @Nonnull String key, @Nullable T object) {
        checkNotNull(transport);
        checkNotNull(key);

        var near = getNearCache();

        var valueClass = (Class<T>) near.getConfig().getValueClass();
        transport.putSingleObject(getName(), key, object, valueClass);
    }

    protected void putRemoteSingle(@Nonnull Transport transport, @Nonnull String key, @Nullable Object object) {
        checkNotNull(transport);
        checkNotNull(key);

        var near = getNearCache();
        var cfg = near.getConfig();
        var serder = cfg.getSerder().getKind().instance;

        var valueBytes = serder.serialize(object);
        long version = transport.putSingle(getName(), key, valueBytes);

        near.putSingle(key, (k, v) -> new ValueImpl(object, valueBytes, version));
    }
}
