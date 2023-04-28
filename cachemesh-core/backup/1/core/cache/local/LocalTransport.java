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

import cachemesh.core.cache.bean.GetResult;
import cachemesh.core.cache.bean.ValueImpl;
import cachemesh.core.cache.transport.Transport;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class LocalTransport implements Transport {

    @Nonnull
    private final LocalCacheManager localCacheManager;

    public LocalTransport(@Nonnull LocalCacheManager localCacheManager) {
        checkNotNull(localCacheManager);

        this.localCacheManager = localCacheManager;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void start(int timeoutSeconds) throws InterruptedException {
        // nothing to do
    }

    @Override
    public void stop(int timeoutSeconds) throws InterruptedException {
        // nothing to do
    }

    @Override
    @Nonnull
    public GetResult<byte[]> getSingle(@Nonnull String cacheName, @Nonnull String key, long version) {
        checkNotNull(cacheName);
        checkNotNull(key);

        var cache = getLocalCacheManager().get(cacheName);
        if (cache == null) {
            return GetResult.notFound();
        }

        var v = cache.getSingle(key);
        if (v == null || v.hasValue() == false) {
            return GetResult.notFound();
        }

        long dataVer = v.getVersion();
        if (dataVer == version) {
            return GetResult.noChange();
        }

        var cfg = cache.getConfig();
        var serder = cfg.getSerder().getKind().instance;
        var data = v.isNullValue() ? null : v.getBytes(serder);
        return GetResult.ok(data, dataVer);
    }

    @Override
    public long putSingle(@Nonnull String cacheName, @Nonnull String key, @Nullable byte[] value) {
        checkNotNull(cacheName);
        checkNotNull(key);

        LocalCache cache = getLocalCacheManager().resolve(cacheName, byte[].class);

        var r = cache.putSingle(key, (k, entry) -> {
            long version = (entry == null) ? 1 : entry.getVersion() + 1;
            return new ValueImpl(value, version);
        });

        return r.getVersion();
    }

    @Override
    @Nullable
    public <T> T getSingleObject(@Nonnull String cacheName, @Nonnull String key) {
        checkNotNull(cacheName);
        checkNotNull(key);

        var cache = getLocalCacheManager().get(cacheName);
        if (cache == null) {
            return null;
        }

        var v = cache.getSingle(key);
        if (v == null || v.hasValue() == false) {
            return null;
        }

        var cfg = cache.getConfig();
        var serder = cfg.getSerder().getKind().instance;
        return v.isNullValue() ? null : v.getObject(serder, cfg.getValueClass());
    }

    @Override
    public <T> long putSingleObject(@Nonnull String cacheName, @Nonnull String key, T value,
            @Nonnull Class<T> valueClass) {
        checkNotNull(cacheName);
        checkNotNull(key);
        checkNotNull(valueClass);

        LocalCache cache = getLocalCacheManager().resolve(cacheName, valueClass);

        var r = cache.putSingle(key, (k, entry) -> {
            long version = (entry == null) ? 1 : entry.getVersion() + 1;
            return new ValueImpl(value, version);
        });

        return r.getVersion();
    }
}
