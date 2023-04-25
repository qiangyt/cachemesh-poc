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

import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.types.BeanType;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.LocalTransport;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.Transport;
import cachemesh.core.spi.TransportProvider;
import lombok.Getter;

@Getter
public abstract class AbstractTransportProvider<T extends Transport, C extends NodeConfig>
        implements TransportProvider {

    private final ShutdownManager shutdownManager;

    private final BeanType<C> configType;

    protected AbstractTransportProvider(BeanType<C> configType, ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
        this.configType = configType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Transport createRemoteTransport(NodeConfig nodeConfig) {
        var c = (C) nodeConfig;
        return doCreateRemoteTransport(c);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean bindLocalTransport(NodeConfig nodeConfig, LocalTransport localTranport) {
        var c = (C) nodeConfig;
        return doBindLocalTransport(c, localTranport);
    }

    protected boolean doBindLocalTransport(C nodeConfig, LocalTransport localTranport) {
        return false;
    }

    protected abstract T doCreateRemoteTransport(C nodeConfig);

    @Override
    public BeanType<? extends NodeConfig> resolveConfigType(TypeRegistry typeRegistry) {
        return getConfigType();
    }

}
