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
package cachemesh.redis.lettuce;

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.MeshNode;
import cachemesh.core.TransportRegistry;
import cachemesh.core.config.LettuceConfig;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.Transport;
import cachemesh.core.spi.TransportProvider;
import lombok.Getter;

@Getter
public class LettuceTransportProvider implements TransportProvider {

    public static final LettuceTransportProvider DEFAULT = new LettuceTransportProvider(
            DedicatedRedisClientProvider.DEFAULT, ShutdownManager.DEFAULT);

    public static void register() {
        TransportRegistry.DEFAULT.register(LettuceConfig.PROTOCOL, DEFAULT);
    }

    private final ShutdownManager shutdownManager;

    private final RedisClientProvider clientProvider;

    public LettuceTransportProvider(RedisClientProvider clientProvider, ShutdownManager shutdownManager) {
        this.clientProvider = clientProvider;
        this.shutdownManager = shutdownManager;
    }

    @Override
    public void afterNodeStop(MeshNode node, int timeoutSeconds) throws InterruptedException {
        var tp = (LettuceTransport) node.getTransport();
        getClientProvider().release(tp.getConfig(), timeoutSeconds);
    }

    @Override
    public String getProtocol() {
        return LettuceConfig.PROTOCOL;
    }

    @Override
    public Transport createRemoteTransport(NodeConfig nodeConfig) {
        var config = (LettuceConfig) nodeConfig;
        var client = getClientProvider().resolve(config);

        var r = new LettuceTransport(config, client, getShutdownManager());
        return r;
    }

}
