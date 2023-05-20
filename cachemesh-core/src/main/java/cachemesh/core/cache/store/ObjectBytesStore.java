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

import cachemesh.core.cache.bean.Value;
import cachemesh.core.cache.bean.ValueStatus;
import cachemesh.core.cache.bean.ValueResult;
import cachemesh.core.cache.spi.LocalCache;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class ObjectBytesStore implements BytesStore {

    @Nonnull
    private final LocalCache<?> objectCache;

    public ObjectBytesStore(@Nonnull LocalCache<?> objectCache) {
        checkNotNull(objectCache);

        this.objectCache = objectCache;
    }

    @Override
    @Nullable
    public ValueResult getSingle(@Nonnull String key, long version) {
        checkNotNull(key);

        var oc = getObjectCache();
        var cfg = objectCache.getConfig();

        var v = oc.getSingle(key);
        if (v == null) {
            return null;
        }

        long storedVer = v.getVersion();
        if (version > 0) {
            if (storedVer == version) {
                return ValueResult.NO_CHANGE;
            }
        }

        if (v.isNull()) {
            return ValueResult.Null(storedVer);
        }

        var serder = cfg.getSerder().getKind().instance;

        var data = v.getData();
        byte[] dataBytes = serder.serialize(data);
        return ValueResult.Ok(dataBytes, storedVer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putSingle(@Nonnull String key, @Nullable ValueResult value) {
        checkNotNull(key);
        checkNotNull(value);

        var oc = getObjectCache();
        var cfg = objectCache.getConfig();

        Value<Object> lVal;
        if (value.getStatus() == ValueStatus.NULL) {
            lVal = Value.Null(value.getVersion());
        } else {
            var serder = cfg.getSerder().getKind().instance;
            Object data = serder.deserialize(value.getData(), cfg.getValueClass());
            lVal = new Value<>(data, value.getVersion());
        }

        ((LocalCache<Object>) oc).putSingle(key, (k, v) -> lVal);
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        checkNotNull(key);
        getObjectCache().invalidateSingle(key);
    }

}
