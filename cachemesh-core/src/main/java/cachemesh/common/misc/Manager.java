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

public abstract class Manager<C, T> extends Registry<C, T> {

    public T resolve(C config) {
        String key = supplyKey(config);
        return getItemMap().computeIfAbsent(key, k -> doCreate(config));
    }

    public T create(C config) {
        String key = supplyKey(config);
        return getItemMap().compute(key, (k, existing) -> {
            if (existing != null) {
                throw new IllegalArgumentException("duplicated: " + key);
            }
            return doCreate(config);
        });
    }

    public T destroy(C config, int timeoutSeconds) throws InterruptedException {
        T r = unregister(config);
        if (r != null) {
            doDestroy(config, r, timeoutSeconds);
        }
        return r;
    }

    protected abstract T doCreate(C config);

    protected abstract void doDestroy(C config, T item, int timeoutSeconds) throws InterruptedException;

}
