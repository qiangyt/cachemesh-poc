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
package cachemesh.common.registry;

public abstract class Manager<KIND, VALUE> extends Registry<KIND, VALUE> {

    public VALUE resolve(KIND kind) {
        return getLocalMap().computeIfAbsent(kind, k -> doCreate(kind));
    }

    public VALUE create(KIND kind) {
        return getLocalMap().compute(kind, (k, existing) -> {
            if (existing != null) {
                var msg = String.format("duplicated %s: %s", getValueName(), kind);
                throw new IllegalArgumentException(msg);
            }
            return doCreate(kind);
        });
    }

    public VALUE destroy(KIND kind, int timeoutSeconds) throws InterruptedException {
        VALUE r = unregister(kind);
        if (r != null) {
            doDestroy(kind, r, timeoutSeconds);
        }
        return r;
    }

    protected abstract VALUE doCreate(KIND kind);

    protected abstract void doDestroy(KIND kind, VALUE value, int timeoutSeconds) throws InterruptedException;

}
