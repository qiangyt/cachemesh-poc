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
import cachemesh.core.cache.bean.ValueResult;
import cachemesh.core.cache.spi.LocalCache;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class DirectBytesStore implements BytesStore {

    @Nonnull
    private final LocalCache<byte[]> bytesCache;

    public DirectBytesStore(@Nonnull LocalCache<byte[]> bytesCache) {
        checkNotNull(bytesCache);

        this.bytesCache = bytesCache;
    }

    @Override
    @Nullable
    public ValueResult getSingle(@Nonnull String key, long version) {
        checkNotNull(key);

        var v = getBytesCache().getSingle(key);
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

        byte[] dataBytes = v.getData();
        return ValueResult.Ok(dataBytes, storedVer);
    }

    @Override
    public void putSingle(@Nonnull String key, @Nonnull ValueResult value) {
        checkNotNull(key);
        checkNotNull(value);

        Value<byte[]> lVal;
        if (value.isNull()) {
            lVal = Value.Null(value.getVersion());
        } else {
            byte[] data = value.getData();
            lVal = new Value<>(data, value.getVersion());
        }

        getBytesCache().putSingle(key, (k, v) -> lVal);
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        checkNotNull(key);
        getBytesCache().invalidateSingle(key);
    }

}
