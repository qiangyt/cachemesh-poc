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
package cachemesh.core.spi.support;

import cachemesh.core.spi.CacheProvider;
import cachemesh.common.shutdown.ShutdownManager;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public abstract class AbstractCacheProvider implements CacheProvider {

    @Nullable
    private final ShutdownManager shutdownManager;

    protected AbstractCacheProvider(@Nullable ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
    }

}
