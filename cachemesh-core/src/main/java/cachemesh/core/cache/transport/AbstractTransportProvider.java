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

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.spi.Transport;
import cachemesh.core.cache.spi.TransportProvider;
import cachemesh.core.config.NodeConfig;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public abstract class AbstractTransportProvider<CACHE extends Transport, CONFIG extends NodeConfig>
        implements TransportProvider<CACHE, CONFIG> {

    @Nullable
    private final ShutdownManager shutdownManager;

    @Nonnull
    private final BeanType<CONFIG> configType;

    protected AbstractTransportProvider(@Nonnull BeanType<CONFIG> configType,
            @Nullable ShutdownManager shutdownManager) {
        checkNotNull(configType);

        this.shutdownManager = shutdownManager;
        this.configType = configType;
    }

    @Override
    @Nonnull
    public BeanType<CONFIG> resolveConfigType(@Nonnull TypeRegistry typeRegistry) {
        checkNotNull(typeRegistry);

        return getConfigType();
    }

}
