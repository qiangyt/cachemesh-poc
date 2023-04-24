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
package cachemesh.common.misc;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class Registry<C, T, S> {

    @Getter(AccessLevel.PROTECTED)
    private Map<String, S> itemMap = createItemMap();

    protected Map<String, S> createItemMap() {
        return new HashMap<>();
    }

    public Iterable<Map.Entry<String, S>> getItems() {
        return getItemMap().entrySet();
    }

    protected abstract S supplyItem(C config, T value);

    protected abstract String supplyKey(C config);

    protected abstract T supplyValue(S item);

    public void register(C config, T value) {
        var key = supplyKey(config);
        getItemMap().compute(key, (k, existing) -> {
            if (existing != null) {
                throw new IllegalArgumentException("duplicated: " + key);
            }
            return supplyItem(config, value);
        });
    }

    public T unregister(C config) {
        String key = supplyKey(config);
        var item = getItemMap().remove(key);
        return (item == null) ? null : supplyValue(item);
    }

    public T load(C config) {
        String key = supplyKey(config);
        return loadByKey(key);
    }

    public T loadByKey(String key) {
        T r = getByKey(key);
        if (r == null) {
            throw new IllegalArgumentException(key + " not found");
        }
        return r;
    }

    public T get(C config) {
        String key = supplyKey(config);
        return getByKey(key);
    }

    public T getByKey(String key) {
        var item = getItemMap().get(key);
        return (item == null) ? null : supplyValue(item);
    }

}
