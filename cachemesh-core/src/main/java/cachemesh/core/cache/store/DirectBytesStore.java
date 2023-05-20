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
public class DirectBytesStore implements BytesStore {

    @Nonnull
    private final LocalCache<byte[]> cache;

    public DirectBytesStore(@Nonnull LocalCache<byte[]> cache) {
        checkNotNull(cache);

        this.cache = cache;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public ValueResult<byte[]> getSingle(@Nonnull String key, long version) {
        checkNotNull(key);

        var cv = getCache().getSingle(key);
        if (cv == null) {
            return null;
        }

        long storedVer = cv.getVersion();
        if (version > 0) {
            if (storedVer == version) {
                return (ValueResult<byte[]>) ValueResult.NO_CHANGE;
            }
        }

        return new ValueResult<>(ValueStatus.OK, cv);
    }

    @Override
    public void putSingle(@Nonnull String key, @Nonnull Value<byte[]> value) {
        checkNotNull(key);
        checkNotNull(value);

        getCache().putSingle(key, (k, v) -> value);
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        checkNotNull(key);
        getCache().invalidateSingle(key);
    }

}
