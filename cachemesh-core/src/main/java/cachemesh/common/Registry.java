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
package cachemesh.common;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class Registry<C, T> {

    @Getter(AccessLevel.PROTECTED)
    private Map<String, T> itemMap;

    protected Map<String, T> createItemMap() {
        return new HashMap<>();
    }

    public void register(C config, T item) {
        var key = retrieveKey(config);
        getItemMap().compute(key, (k, existing) -> {
            if (existing != null) {
                throw new IllegalArgumentException("duplicated: " + key);
            }
            return item;
        });
    }

    public T unregister(C config) {
        String key = retrieveKey(config);
        return getItemMap().remove(key);
    }

    public T get(C config) {
        String key = retrieveKey(config);
        return getByKey(key);
    }

    public T getByKey(String key) {
        return getItemMap().get(key);
    }

    protected abstract String retrieveKey(C config);

}
