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
package cachemesh.core.cache.local;

import cachemesh.core.cache.bean.BytesValue;
import cachemesh.core.cache.spi.LocalCache;
import cachemesh.core.cache.spi.Transport;

public class InternalLocalTransport<T> {

    private final LocalCache<T> cache;

    public BytesValue getSingle(String key, long version) {

    }

    @Override
    public void putSingle(String key, byte[] bytes) {

    }

}
