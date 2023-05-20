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

import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class CombinedBytesStore implements BytesStore {

    @Nonnull
    private final ObjectBytesStore objectStore;

    @Nonnull
    private final DirectBytesStore directStore;

    public CombinedBytesStore(@Nonnull ObjectBytesStore objectStore, DirectBytesStore directStore) {
        checkNotNull(objectStore);
        checkNotNull(directStore);

        this.objectStore = objectStore;
        this.directStore = directStore;
    }

    @Override
    @Nullable
    public ValueResult<byte[]> getSingle(@Nonnull String key, long version) {
        checkNotNull(key);

        var dstore = getDirectStore();
        var ostore = getObjectStore();

        var dv = dstore.getSingle(key, version);
        if (dv != null && dv.getStatus() == ValueStatus.OK) {
            version = dv.getValue().getVersion();
        }

        var r = ostore.getSingle(key, version);
        if (r == null) {
            dstore.removeSingle(key);
            return null;
        }

        if (r.getStatus() == ValueStatus.OK) {
            dstore.putSingle(key, r.getValue());
        }
        return r;
    }

    @Override
    public void putSingle(@Nonnull String key, @Nonnull Value<byte[]> value) {
        checkNotNull(key);

        getObjectStore().putSingle(key, value);
        getDirectStore().putSingle(key, value);
    }

    @Override
    public void removeSingle(@Nonnull String key) {
        checkNotNull(key);

        getObjectStore().removeSingle(key);
        getDirectStore().removeSingle(key);
    }

}
