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
package cachemesh.core.cache.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.cache.store.ValueResult;

public interface Transport extends Shutdownable {

    void open(int timeoutSeconds) throws InterruptedException;

    boolean isRemote();

    @Nullable
    ValueResult getSingle(@Nonnull String cacheName, @Nonnull String key, long version);

    void putSingle(@Nonnull String cacheName, @Nonnull String key, @Nullable byte[] bytes);

}
