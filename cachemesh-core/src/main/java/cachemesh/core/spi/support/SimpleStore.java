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

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.core.bean.Value;
import cachemesh.core.bean.ValueResult;
import cachemesh.core.bean.ValueStatus;
import cachemesh.core.spi.Cache;
import cachemesh.core.spi.InternalStore;
import lombok.Getter;

@Getter
public class SimpleStore<T> implements InternalStore<T> {

    @Nonnull
    private final Cache<T> cache;

    public SimpleStore(Cache<T> cache) {
        this.cache = requireNonNull(cache);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public ValueResult<T> getSingle(@Nonnull String key, long version, @Nullable Function<String, Value<T>> loader) {
        requireNonNull(key);

        var cv = getCache().getSingle(key, loader);
        if (cv == null) {
            return null;
        }

        long storedVer = cv.getVersion();
        if (version > 0) {
            if (storedVer == version) {
                return (ValueResult<T>) ValueResult.NO_CHANGE;
            }
        }

        return new ValueResult<>(ValueStatus.OK, cv);
    }

    @Override
    public Value<T> putSingle(@Nonnull String key, @Nullable T value) {
        requireNonNull(key);

        return getCache().putSingle(key, (k, oldValue) -> {
            long ver = (oldValue == null) ? System.currentTimeMillis() : oldValue.getVersion() + 1;
            return new Value<>(value, ver);
        });
    }

    @Override
    public void putSingleInternal(@Nonnull String key, @Nonnull Value<T> value) {
        requireNonNull(key);
        requireNonNull(value);

        getCache().putSingle(key, (k, oldValue) -> {
            return new Value<>(value.getData(), value.getVersion());
        });
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        requireNonNull(key);
        getCache().removeSingle(key);
    }

}
