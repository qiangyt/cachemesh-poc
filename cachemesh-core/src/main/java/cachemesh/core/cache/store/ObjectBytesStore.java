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
package cachemesh.core.cache.store;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.core.cache.spi.LocalCache;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class ObjectBytesStore implements BytesStore {

    @Nonnull
    private final LocalCache<?> cache;

    public ObjectBytesStore(@Nonnull LocalCache<?> cache) {
        checkNotNull(cache);

        this.cache = cache;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public ValueResult<byte[]> getSingle(@Nonnull String key, long version) {
        checkNotNull(key);

        var c = getCache();

        var objV = c.getSingle(key);
        if (objV == null) {
            return null;
        }

        long storeVer = objV.getVersion();
        if (version > 0) {
            if (storeVer == version) {
                return (ValueResult<byte[]>) ValueResult.NO_CHANGE;
            }
        }

        Value<byte[]> v;

        var data = objV.getData();
        if (data == null) {
            v = new Value<>(null, storeVer);
        } else {
            var serder = c.getConfig().getSerder().getKind().instance;
            var dataBytes = serder.serialize(data);
            v = new Value<>(dataBytes, storeVer);
        }

        return new ValueResult<>(ValueStatus.OK, v);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putSingle(@Nonnull String key, @Nonnull Value<byte[]> value) {
        checkNotNull(key);
        checkNotNull(value);

        var c = getCache();

        Value<Object> cv;

        var dataBytes = value.getData();
        if (dataBytes == null) {
            cv = new Value<>(null, value.getVersion());
        } else {
            var cfg = c.getConfig();
            var serder = cfg.getSerder().getKind().instance;
            var data = serder.deserialize(dataBytes, cfg.getValueClass());
            cv = new Value<>(data, value.getVersion());
        }

        ((LocalCache<Object>) c).putSingle(key, (k, v) -> cv);
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        checkNotNull(key);
        getCache().invalidateSingle(key);
    }

}
