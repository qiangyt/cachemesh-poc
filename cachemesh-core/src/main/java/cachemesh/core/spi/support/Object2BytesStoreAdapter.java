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
import cachemesh.core.spi.BytesStore;
import cachemesh.core.spi.ObjectStore;
import lombok.Getter;

@Getter
public class Object2BytesStoreAdapter implements BytesStore {

    private final ObjectStore objectStore;

    public Object2BytesStoreAdapter(ObjectStore objectStore) {
        requireNonNull(objectStore);
        
        this.objectStore = objectStore;
    }

    @Override
    @Nonnull 
    public CacheConfig getConfig() {
        return getObjectStore().getConfig();
    }

    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ValueResult<byte[]> getSingle(@Nonnull String key, long version, @Nullable Function<String, Value<byte[]>> loader) {
        requireNonNull(key);
        
        if (loader == null) {
            // if loader is null, get the object from object store then try to serialize the object to bytes
            var objResult = getObjectStore().getSingle(key, version, null);
            if (objResult == null) {
                // not found
                return null;
            }
            if (objResult.getStatus() == ValueStatus.NO_CHANGE) {
                // no change
                return (ValueResult<byte[]>) ValueResult.NO_CHANGE;
            }

            // does have object value
            var objV = objResult.getValue(); 

            var data = objV.getData();
            if (data == null) {
                // if data is null, the objResult could be reused as ValueResult<byte[]>
                return (ValueResult)objResult;
            }

            var cfg = getConfig();
            var serder = cfg.getSerder().getKind().instance;
        
            // if have non-null object value, serialize the object 
            var dataBytes = serder.serialize(data);
            var v = new Value<>(dataBytes, objV.getVersion());
                
            return new ValueResult<>(ValueStatus.OK, v);    
        }
        
        // if load is not null
        var vholder = new Holder<Value<byte[]>>();

        var objResult = getObjectStore().getSingle(key, version, k -> {
            // if not found, then call loader to get bytes, deserialize it as object and populate it into object store
            var v = loader.apply(k);
            if (v == null) {
                return null;
            }
            vholder.set(v);

            Value<Object> newObjV;

            var newBytes = v.getData();
            if (newBytes == null) {
                newObjV = (Value)v;
            } else {
                var cfg = getConfig();
                var serder = cfg.getSerder().getKind().instance;
        
                Object newObj = serder.deserialize(newBytes, cfg.getValueClass());
                newObjV = new Value<>(newObj, v.getVersion());
            }

            return newObjV;
        });
        
        if (objResult == null) {
            return null;
        }

        var v = vholder.get();
        if (v != null) {
            // reuse the bytes which is from the loader
            return new ValueResult<>(ValueStatus.OK, v);
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
        v = new Value<>(dataBytes, objV.getVersion());

        return new ValueResult<>(ValueStatus.OK, v);     
    }

    @Override
    public void putSingle(@Nonnull String key, @Nullable byte[] value) {
        requireNonNull(key);

        Object objV;
        if (value == null) {
            objV = null;
        } else {
            var cfg = getConfig();
            var serder = cfg.getSerder().getKind().instance;
            objV = serder.deserialize(value, cfg.getValueClass());
        }

        getObjectStore().putSingle(key, objV);
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        requireNonNull(key);
        getObjectStore().removeSingle(key);
    }

}
