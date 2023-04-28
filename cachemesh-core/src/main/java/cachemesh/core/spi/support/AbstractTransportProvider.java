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

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.LocalTransport;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.Transport;
import cachemesh.core.spi.TransportProvider;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public abstract class AbstractTransportProvider<T extends Transport, C extends NodeConfig>
        implements TransportProvider {

    @Nullable
    private final ShutdownManager shutdownManager;

    @Nonnull
    private final BeanType<C> configType;

    protected AbstractTransportProvider(@Nonnull BeanType<C> configType, @Nullable ShutdownManager shutdownManager) {
        checkNotNull(configType);

        this.shutdownManager = shutdownManager;
        this.configType = configType;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nonnull
    public Transport createRemoteTransport(@Nonnull NodeConfig nodeConfig) {
        checkNotNull(nodeConfig);

        var c = (C) nodeConfig;
        return doCreateRemoteTransport(c);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nonnull
    public boolean bindLocalTransport(@Nonnull NodeConfig nodeConfig, @Nonnull LocalTransport localTranport) {
        checkNotNull(nodeConfig);
        checkNotNull(localTranport);

        var c = (C) nodeConfig;
        return doBindLocalTransport(c, localTranport);
    }

    protected boolean doBindLocalTransport(C nodeConfig, LocalTransport localTranport) {
        checkNotNull(nodeConfig);
        checkNotNull(localTranport);

        return false;
    }

    @Nonnull
    protected abstract T doCreateRemoteTransport(@Nonnull C nodeConfig);

    @Override
    @Nonnull
    public BeanType<? extends NodeConfig> resolveConfigType(@Nonnull TypeRegistry typeRegistry) {
        checkNotNull(typeRegistry);

        return getConfigType();
    }

}
