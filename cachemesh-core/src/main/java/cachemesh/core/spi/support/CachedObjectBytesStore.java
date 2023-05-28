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

import cachemesh.common.misc.Holder;
import cachemesh.core.bean.Value;
import cachemesh.core.bean.ValueResult;
import cachemesh.core.bean.ValueStatus;
import cachemesh.core.spi.InternalStore;
import cachemesh.core.spi.Store;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

@Getter
public class CachedObjectBytesStore implements Store<byte[]> {

    @Nonnull
    private final ObjectBytesStore objectBytesStore;

    @Nonnull
    private final InternalStore<byte[]> cachingBytesStore;

    public CachedObjectBytesStore(@Nonnull ObjectBytesStore objectBytesStore, @Nonnull InternalStore<byte[]> cachingBytesStore) {
        this.objectBytesStore = requireNonNull(objectBytesStore);
        this.cachingBytesStore = requireNonNull(cachingBytesStore);
    }

    @Override
    @Nullable
    public ValueResult<byte[]> getSingle(@Nonnull String key, final long version, @Nullable Function<String, Value<byte[]>> loader) {
        requireNonNull(key);

        var bstore = getCachingBytesStore();
        var obstore = getObjectBytesStore();
        var objResultHolder = new Holder<ValueResult<byte[]>>();

        var bv = bstore.getSingle(key, version, k -> {
            var objResult = obstore.getSingle(key, version, loader);
            if (objResult == null) {
                // not found
                return null;
            }
            objResultHolder.set(objResult);

            if (objResult.getStatus() == ValueStatus.NO_CHANGE) {
                // no change
                return null;
            }

            // does have object value
            return objResult.getValue();
        });

        var objResult = objResultHolder.get();
        if (objResult != null) {
            // already reached the obstore
            return objResult;
        }

        // not yet reached the obstore or obstore also returns null 
        if (bv == null) {
            return null;
        }
        
        long ver;
        if (bv.getStatus() == ValueStatus.NO_CHANGE) {
            ver = version;
        } else {
            ver = bv.getValue().getVersion();
        }

        objResult = obstore.getSingle(key, ver, null);
        if (objResult == null) {
            return bv;
        }
        if (objResult.getStatus() == ValueStatus.NO_CHANGE) {
            return bv;
        }

        bstore.putSingleInternal(key, objResult.getValue());
        return objResult;
    }

    @Override
    public Value<byte[]> putSingle(@Nonnull String key, @Nullable byte[] value) {
        requireNonNull(key);

        var r = getObjectBytesStore().putSingle(key, value);
        getCachingBytesStore().putSingleInternal(key, r);
        return r;
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        requireNonNull(key);

        getObjectBytesStore().removeSingle(key);
        getCachingBytesStore().removeSingle(key);
    }

}
