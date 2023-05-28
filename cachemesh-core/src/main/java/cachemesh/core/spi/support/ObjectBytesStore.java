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
import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import cachemesh.common.misc.Holder;
import cachemesh.core.bean.Value;
import cachemesh.core.bean.ValueResult;
import cachemesh.core.bean.ValueStatus;
import cachemesh.core.config.CacheConfig;
import cachemesh.core.spi.InternalStore;
import lombok.Getter;

@Getter
public class ObjectBytesStore implements InternalStore<byte[]> {

    @Nonnull
    private final CacheConfig config;
    
    @Nonnull
    private final InternalStore<Object> objectStore;

    public ObjectBytesStore(CacheConfig config, InternalStore<Object> objectStore) {
        this.config = requireNonNull(config);
        this.objectStore = requireNonNull(objectStore);
    }

    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ValueResult<byte[]> getSingle(@Nonnull String key, long version, @Nullable Function<String, Value<byte[]>> loader) {
        requireNonNull(key);
        
        var ostore = getObjectStore();
        ValueResult<Object> objResult;

        if (loader == null) {
            // if loader is null, get the object from object store then try to serialize the object to bytes
            objResult = ostore.getSingle(key, version, null);
        } else {        
            // if load is not null
            var vholder = new Holder<Value<byte[]>>();

            objResult = getObjectStore().getSingle(key, version, k -> {
                // if not found, then call loader to get bytes, deserialize it as object and populate it into object store
                var v = loader.apply(k);
                if (v == null) {
                    return null;
                }
                vholder.set(v);
                
                var newObj = deserialize(v.getData());
                return (newObj == null) ? (Value)v : new Value<>(newObj, v.getVersion());
            });

            var v = vholder.get();
            if (v != null) {
                // reuse the bytes which is from the loader
                return new ValueResult<>(ValueStatus.OK, v);
            }
        }

        if (objResult == null) {
            // not found
            return null;
        }
        if (objResult.getStatus() == ValueStatus.NO_CHANGE) {
            // no change
            return (ValueResult<byte[]>) ValueResult.NO_CHANGE;
        }

        // the value is not from loader, instead, it is from existing cached object, so should serialize it
        var objV = objResult.getValue();

        var data = objV.getData();
        if (data == null) {
            // if data is null, the objResult could be reused as ValueResult<byte[]>
            return (ValueResult) objResult;
        }

        var cfg = getConfig();
        var serder = cfg.getSerder().getKind().instance;
        
        // if have non-null object value, serialize the object
        var dataBytes = serder.serialize(data);
        var v = new Value<>(dataBytes, objV.getVersion());

        return new ValueResult<>(ValueStatus.OK, v);     
    }

    public Object deserialize(@Nullable byte[] value) {
        if (value == null) {
            return null;
        }
        
        var cfg = getConfig();
        var serder = cfg.getSerder().getKind().instance;
        return serder.deserialize(value, cfg.getValueClass());
    }

    @Override
    public Value<byte[]> putSingle(@Nonnull String key, @Nullable byte[] value) {
        requireNonNull(key);

        Object objV = deserialize(value);
        var objResult = getObjectStore().putSingle(key, objV);
        return new Value<>(value, objResult.getVersion());
    }

    @Override
    public void putSingleInternal(@Nonnull String key, @Nonnull Value<byte[]> value) {
        requireNonNull(key);
        requireNonNull(value);

        Object objV = deserialize(value.getData());
        getObjectStore().putSingleInternal(key, new Value<>(objV, value.getVersion()));
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        requireNonNull(key);
        getObjectStore().removeSingle(key);
    }

}
