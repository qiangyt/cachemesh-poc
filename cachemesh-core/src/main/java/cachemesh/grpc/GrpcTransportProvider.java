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

import cachemesh.common.config.op.BeanOp;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.LocalTransport;
import cachemesh.core.MeshNode;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.Transport;
import cachemesh.core.spi.TransportProvider;
import lombok.Getter;

@Getter
public class GrpcTransportProvider implements TransportProvider {

    private final GrpcServerProvider serverProvider;

    private final ShutdownManager shutdownManager;

    public GrpcTransportProvider() {
        this(GrpcServerProvider.DEFAULT, ShutdownManager.DEFAULT);
    }

    public GrpcTransportProvider(GrpcServerProvider serverProvider, ShutdownManager shutdownManager) {
        this.serverProvider = serverProvider;
        this.shutdownManager = shutdownManager;
    }

    @Override
    public void beforeNodeStart(MeshNode node, int timeoutSeconds) throws InterruptedException {
        var cfg = (GrpcNodeConfig) node.getConfig();
        if (cfg.isLocal() == false) {
            return;
        }

        var server = getServerProvider().get(cfg);
        server.start(timeoutSeconds);
    }

    @Override
    public boolean bindLocalTransport(NodeConfig transportConfig, LocalTransport localTranport) {
        var config = (GrpcNodeConfig) transportConfig;
        var server = new DedicatedGrpcServer(config, getShutdownManager());

        if (server.isStarted()) {
            throw new IllegalStateException("should NOT add service after grpc server is started");
        }
        var service = new GrpcService(config, localTranport);
        server.addService(service);

        return true;
    }

    /*
     * @Override public NodeConfig parseConfig(Map<String, Object> configMap) { return GrpcConfig.from(configMap); }
     */

    @Override
    public Transport createRemoteTransport(NodeConfig nodeConfig) {
        var config = (GrpcNodeConfig) nodeConfig;
        return new GrpcTransport(config, getShutdownManager());
    }

	@Override
	public BeanOp<? extends NodeConfig> configOp() {

	}
}
