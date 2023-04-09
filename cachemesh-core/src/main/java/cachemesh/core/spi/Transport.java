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
package cachemesh.core.spi;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.core.bean.GetResult;

@ThreadSafe
public interface Transport {

    void start(int timeoutSeconds) throws InterruptedException;

    void stop(int timeoutSeconds) throws InterruptedException;

    GetResult<byte[]> getSingle(String cacheName, String key, long version);

    // return version
    long putSingle(String cacheName, String key, byte[] value);

    boolean isRemote();

    default <T> T getSingleObject(String cacheName, String key) {
        if (isRemote()) {
            throw new UnsupportedOperationException("unsupported by remote transport");
        }
        throw new UnsupportedOperationException("to be implemented");
    }

    default <T> long putSingleObject(String cacheName, String key, T value, Class<T> valueClass) {
        if (isRemote()) {
            throw new UnsupportedOperationException("unsupported by remote transport");
        }
        throw new UnsupportedOperationException("to be implemented");
    }

}
