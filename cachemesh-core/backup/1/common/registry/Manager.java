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

import cachemesh.common.err.BadValueException;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

public abstract class Manager<KIND, VALUE> extends Registry<KIND, VALUE> {

    @Nonnull
    public VALUE resolve(@Nonnull KIND kind) {
        checkNotNull(kind);

        return getLocalMap().computeIfAbsent(kind, k -> doCreate(kind));
    }

    @Nonnull
    public VALUE create(@Nonnull KIND kind) {
        checkNotNull(kind);

        return getLocalMap().compute(kind, (k, existing) -> {
            if (existing != null) {
                throw new BadValueException("duplicated %s: %s", getValueName(), kind);
            }
            return doCreate(kind);
        });
    }

    @Nonnull
    public VALUE destroy(@Nonnull KIND kind, int timeoutSeconds) throws InterruptedException {
        checkNotNull(kind);

        VALUE r = unregister(kind);
        if (r != null) {
            doDestroy(kind, r, timeoutSeconds);
        }
        return r;
    }

    @Nonnull
    protected abstract VALUE doCreate(@Nonnull KIND kind);

    @Nonnull
    protected abstract void doDestroy(@Nonnull KIND kind, @Nonnull VALUE value, int timeoutSeconds)
            throws InterruptedException;

}
