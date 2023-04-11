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

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.LocalTransport;
import cachemesh.core.MeshNode;
import cachemesh.core.spi.support.AbstractTransportProvider;
import lombok.Getter;

@Getter
public class GrpcTransportProvider extends AbstractTransportProvider<GrpcTransport, GrpcConfig> {

    private final GrpcServerProvider serverProvider;

    public GrpcTransportProvider() {
        this(GrpcServerProvider.DEFAULT, ShutdownManager.DEFAULT);
    }

    public GrpcTransportProvider(GrpcServerProvider serverProvider, ShutdownManager shutdownManager) {
        super(GrpcConfig.class, shutdownManager);
        this.serverProvider = serverProvider;

    }

    @Override
    public void beforeNodeStart(MeshNode node, int timeoutSeconds) throws InterruptedException {
        var cfg = (GrpcConfig) node.getConfig();
        if (cfg.isLocal() == false) {
            return;
        }

        var server = getServerProvider().get(cfg);
        server.start(timeoutSeconds);
    }

    @Override
    protected boolean doBindLocalTransport(GrpcConfig config, LocalTransport localTranport) {
        var server = new DedicatedGrpcServer(config, getShutdownManager());

        if (server.isStarted()) {
            throw new IllegalStateException("should NOT add service after grpc server is started");
        }
        var service = new GrpcService(config, localTranport);
        server.addService(service);

        return true;
    }

    @Override
    protected GrpcTransport doCreateRemoteTransport(GrpcConfig config) {
        return new GrpcTransport(config, getShutdownManager());
    }

}
