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
import cachemesh.core.config.CacheConfig;
import cachemesh.core.spi.GenericStore;
import cachemesh.core.spi.ValueLoader;
import cachemesh.core.spi.ValueMapper;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractGenericStore<T> implements GenericStore<T> {

    private final CacheConfig config;

    public AbstractGenericStore(CacheConfig config) {
        this.config = config;
    }

    @Override
    @Nonnull 
    public CacheConfig getConfig() {
        return this.config;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public ValueResult<T> getSingle(@Nonnull String key, long version, @Nullable ValueLoader loader) {
        requireNonNull(key);

        var cv = doGetSingle(key, loader);
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

    @Nullable 
    protected abstract Value<T> doGetSingle(@Nonnull String key, @Nullable ValueLoader loader);

    @Override
    public void putSingle(@Nonnull String key, @Nullable T value) {
        requireNonNull(key);
        requireNonNull(value);

        doPutSingle(key, (k, oldValue) -> {
            long ver = (oldValue == null) ? 1 : oldValue.getVersion();
            return new Value<T>(value, ver);
        });
    }

    protected abstract void doPutSingle(@Nonnull String key, @Nonnull ValueMapper mapper);

    @Override
    public void removeSingle(@Nonnull String key) {
        requireNonNull(key);
        doRemoveSingle(key);
    }

    protected abstract void doRemoveSingle(@Nonnull String key);

}
