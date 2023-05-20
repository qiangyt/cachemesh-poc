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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.common.err.BadStateException;
import cachemesh.common.misc.Serderializer;
import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.cache.bean.Value;
import cachemesh.core.cache.spi.LocalCache;
import cachemesh.core.cache.spi.Transport;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Getter;

@Getter
public class CombinedCache<T> implements Shutdownable {

    @Nonnull
    private final LocalCache<T> localCache;

    @Nonnull
    private final String name;

    @Nonnull
    private final Class<T> valueClass;

    @Nonnull
    private final Serderializer serder;

    @Nonnull
    private final Transport transport;

    @SuppressWarnings("unchecked")
    public CombinedCache(@Nonnull LocalCache<T> localCache, @Nonnull Transport transport) {
        checkNotNull(localCache);
        checkNotNull(transport);

        this.localCache = localCache;
        this.transport = transport;

        var cfg = localCache.getConfig();
        this.name = cfg.getName();
        this.valueClass = (Class<T>) cfg.getValueClass();
        this.serder = cfg.getSerder().getKind().instance;
    }

    public void open(int timeoutSeconds) throws InterruptedException {
        getLocalCache().open(timeoutSeconds);

        if (getTransport().isRemote()) {
            getTransport().open(timeoutSeconds);
        }
    }

    @Override
    public void shutdown(int timeoutSeconds) throws InterruptedException {
        try {
            if (getTransport().isRemote()) {
                getTransport().shutdown(timeoutSeconds);
            }
        } finally {
            getLocalCache().shutdown(timeoutSeconds);
        }
    }

    @Nullable
    public Value<T> getSingle(@Nonnull String key) {
        checkNotNull(key);

        var lCache = getLocalCache();
        var tp = getTransport();

        var r = lCache.getSingle(key);
        if (tp.isRemote() == false) {
            return r;
        } else {
            long ver = (r == null) ? 0 : r.getVersion();

            var rVal = tp.getSingle(getName(), key, ver);
            if (rVal == null) {
                lCache.invalidateSingle(key);
                return null;
            }

            var status = rVal.getStatus();

            switch (status) {
            case OK: {
                T data = getSerder().deserialize(rVal.getBytes(), getValueClass());
                return lCache.putSingle(key, (k, v) -> new Value<T>(data, rVal.getVersion()));
            }
            case NO_CHANGE:
                return r;
            case NULL: {
                return lCache.putSingle(key, (k, v) -> Value.Null(rVal.getVersion()));
            }
            default:
                throw new BadStateException("unexpected status: %s", status);
            }
        }
    }

    public void putSingle(@Nonnull String key, @Nullable T value) {
        checkNotNull(key);

        var tp = getTransport();
        if (tp.isRemote()) {
            var bytes = getSerder().serialize(value);
            tp.putSingle(getName(), key, bytes);
        }

        getLocalCache().invalidateSingle(key);
    }

}
