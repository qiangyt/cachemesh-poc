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
package cachemesh.core.cache.transport;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.core.cache.bean.GetResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@ThreadSafe
public interface Transport {

    void start(int timeoutSeconds) throws InterruptedException;

    void stop(int timeoutSeconds) throws InterruptedException;

    @Nonnull
    GetResult<byte[]> getSingle(@Nonnull String cacheName, @Nonnull String key, long version);

    // return version
    long putSingle(@Nonnull String cacheName, @Nonnull String key, @Nullable byte[] value);

    boolean isRemote();

    @Nullable
    default <T> T getSingleObject(@Nonnull String cacheName, @Nonnull String key) {
        checkNotNull(cacheName);
        checkNotNull(key);

        if (isRemote()) {
            throw new UnsupportedOperationException("unsupported by remote transport");
        }
        throw new UnsupportedOperationException("to be implemented");
    }

    default <T> long putSingleObject(@Nonnull String cacheName, @Nonnull String key, @Nullable T value,
            @Nonnull Class<T> valueClass) {
        checkNotNull(cacheName);
        checkNotNull(key);
        checkNotNull(valueClass);

        if (isRemote()) {
            throw new UnsupportedOperationException("unsupported by remote transport");
        }
        throw new UnsupportedOperationException("to be implemented");
    }

}
