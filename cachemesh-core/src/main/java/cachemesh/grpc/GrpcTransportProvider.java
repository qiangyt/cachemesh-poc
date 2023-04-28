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
package cachemesh.grpc;

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.ReflectBeanType;
import cachemesh.common.err.BadStateException;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.MeshNode;
import cachemesh.core.cache.local.LocalTransport;
import cachemesh.core.cache.transport.AbstractTransportProvider;
import lombok.Getter;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class GrpcTransportProvider extends AbstractTransportProvider<GrpcTransport, GrpcConfig> {

    @Nonnull
    private final GrpcServerProvider serverProvider;

    public GrpcTransportProvider(@Nonnull TypeRegistry typeRegistry, @Nonnull GrpcServerProvider serverProvider,
            @Nonnull ShutdownManager shutdownManager) {

        super(ReflectBeanType.of(typeRegistry, GrpcConfig.class), shutdownManager);

        checkNotNull(serverProvider);

        this.serverProvider = serverProvider;

    }

    @Override
    public void beforeNodeStart(@Nonnull MeshNode node, int timeoutSeconds) throws InterruptedException {
        checkNotNull(node);

        var cfg = (GrpcConfig) node.getConfig();
        if (cfg.isLocal() == false) {
            return;
        }

        var server = getServerProvider().get(cfg);
        server.start(timeoutSeconds);
    }

    @Override
    public boolean bindLocalTransport(@Nonnull GrpcConfig config, @Nonnull LocalTransport localTranport) {
        checkNotNull(config);
        checkNotNull(localTranport);

        var server = new DedicatedGrpcServer(config, getShutdownManager());

        if (server.isStarted()) {
            throw new BadStateException("should NOT add service after grpc server is started");
        }
        var service = new GrpcService(config, localTranport);
        server.addService(service);

        return true;
    }

    @Override
    @Nonnull
    public GrpcTransport createRemoteTransport(@Nonnull GrpcConfig config) {
        checkNotNull(config);

        return new GrpcTransport(config, getShutdownManager());
    }

}
