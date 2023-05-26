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

import cachemesh.core.config.CacheConfig;
import cachemesh.core.spi.Cache;
import lombok.Getter;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static java.util.Objects.requireNonNull;

@Getter
public abstract class AbstractCache extends AbstractShutdownable implements Cache  {

    @Nonnull
    private final CacheConfig config;

    public AbstractCache(@Nonnull CacheConfig config, @Nullable ShutdownManager shutdownManager) {
        super(config.getName(), shutdownManager);

        requireNonNull(config);
        this.config = config;
    }

    @Override
    public String toString() {
        return getConfig().toString();
    }

    @Override
    public void open(int timeoutSeconds) throws InterruptedException {
        // no nothing by default
    }

}
