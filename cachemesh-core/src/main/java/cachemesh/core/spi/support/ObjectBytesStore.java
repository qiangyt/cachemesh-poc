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

import cachemesh.common.misc.Holder;
import cachemesh.core.bean.Value;
import cachemesh.core.bean.ValueResult;
import cachemesh.core.bean.ValueStatus;
import cachemesh.core.config.CacheConfig;
import cachemesh.core.spi.BytesStore;
import lombok.Getter;

import java.util.function.Function;

public class ObjectBytesStore extends AbstractGenericStore<byte[]> implements BytesStore {

    public ObjectBytesStore(CacheConfig config) {
        super(config);
    }


    @Nullable 
    protected Value<Object> doGetSingleObject(@Nonnull String key, @Nullable Function<String, Value<Object>> loader) {
        return null;
    }


    @Nullable 
    protected Value<byte[]> doGetSingle(@Nonnull String key, @Nullable Function<String, Value<byte[]>> loader) {
        Value<Object> objV;
        
        if (loader == null) {
            objV = doGetSingleObject(key, null);
        } else {
            var vholder = new Holder<Value<byte[]>>();
            objV = doGetSingleObject(key, k -> {
                var v = loader.apply(k);
                if (v == null) {
                    return null;
                }
                vholder.set(v);

                Value<Object> newObjV;

                var newBytes = v.getData();
                if (newBytes == null) {
                    newObjV = new Value<>(null, v.getVersion());
                } else {
                    var cfg = getConfig();
                    var serder = cfg.getSerder().getKind().instance;
                    var newObj = serder.deserialize(newBytes, cfg.getValueClass());
                    newObjV = new Value<>(newObj, v.getVersion());
                }

                return newObjV;
            });

            return vholder.get();
        }
        
        var serder = getConfig().getSerder().getKind().instance;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public ValueResult<byte[]> getSingle(@Nonnull String key, long version,
            @Nullable Function<String, Value<byte[]>> loader) {
        requireNonNull(key);

        var ch = getCache();
        var cfg = ch.getConfig();
        var serder = cfg.getSerder().getKind().instance;

        Value<Object> objV;

        if (loader == null) {
            objV = ch.getSingle(key, null);
        } else {
            var vholder = new Holder<Value<byte[]>>();
            objV = ch.getSingle(key, k -> {
                var v = loader.apply(k);
                if (v == null) {
                    return null;
                }
                vholder.set(v);

                Value<Object> newObjV;

                var newBytes = v.getData();
                if (newBytes == null) {
                    newObjV = new Value<>(null, v.getVersion());
                } else {
                    var newObj = serder.deserialize(newBytes, cfg.getValueClass());
                    newObjV = new Value<>(newObj, v.getVersion());
                }

                return newObjV;
            });

            var v = vholder.get();
            if (v != null) {
                return new ValueResult<>(ValueStatus.OK, v);
            }
        }

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
            var dataBytes = serder.serialize(data);
            v = new Value<>(dataBytes, storeVer);
        }

        return new ValueResult<>(ValueStatus.OK, v);
    }

    @Override
    public void putSingle(@Nonnull String key, @Nullable byte[] value) {
        requireNonNull(key);

        var c = getCache();

        c.putSingle(key, (k, oldValue) -> {
            long ver = (oldValue == null) ? 1 : oldValue.getVersion();

            if (value == null) {
                return new Value<>(null, ver);
            }

            var cfg = c.getConfig();
            var serder = cfg.getSerder().getKind().instance;
            var data = serder.deserialize(value, cfg.getValueClass());
            return new Value<>(data, ver);
        });
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        requireNonNull(key);
        getCache().invalidateSingle(key);
    }

}
