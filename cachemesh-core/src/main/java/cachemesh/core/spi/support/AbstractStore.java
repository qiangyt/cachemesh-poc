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
package cachemesh.core.spi.support;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.core.bean.Value;
import cachemesh.core.bean.ValueResult;
import cachemesh.core.bean.ValueStatus;
import cachemesh.core.spi.BytesStore;
import cachemesh.core.spi.ValueLoader;

import java.util.function.BiFunction;
import static java.util.Objects.requireNonNull;

public abstract class AbstractStore implements BytesStore {

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public ValueResult<byte[]> getSingle(@Nonnull String key, long version,
            @Nullable ValueLoader loader) {
        requireNonNull(key);

        var cv = doGetSingle(key, loader);
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

    @Nullable 
    protected abstract Value<byte[]> doGetSingle(@Nonnull String key, @Nullable ValueLoader loader);

    @Override
    public void putSingle(@Nonnull String key, @Nullable byte[] value) {
        requireNonNull(key);
        requireNonNull(value);

        doPutSingle(key, (k, oldValue) -> {
            long ver = (oldValue == null) ? 1 : oldValue.getVersion();
            return new Value<byte[]>(value, ver);
        });
    }

    protected abstract void doPutSingle(@Nonnull String key, @Nonnull BiFunction<String, Value<byte[]>, Value<byte[]>> mapper);

    @Override
    public void removeSingle(@Nonnull String key) {
        requireNonNull(key);
        doRemoveSingle(key);
    }

    protected abstract void doRemoveSingle(@Nonnull String key);

}
