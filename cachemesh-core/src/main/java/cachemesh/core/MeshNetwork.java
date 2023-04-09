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
package cachemesh.core;

import org.slf4j.Logger;

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.common.shutdown.Shutdownable;
import lombok.Getter;

import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.misc.LifeStage;
import cachemesh.common.misc.LogHelper;
import cachemesh.core.config.MeshConfig;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.Transport;
import cachemesh.core.spi.TransportProvider;
import lombok.AccessLevel;

@Getter
public class MeshNetwork implements Shutdownable {

    private final MeshConfig config;

    @Getter(AccessLevel.PROTECTED)
    private final ConsistentHash<MeshNode> route;

    private final LocalCacheManager localCacheManager;

    private final Transports transportRegistry;

    private final Logger logger;

    private final LifeStage lifeStage;

    private final MeshCacheManager meshCacheManager;

    public static MeshNetwork build(MeshConfig config) {
        var name = config.getName();

        var localConfig = config.getLocal();
        var localCacheProvider = localConfig.getKind().provider;
        var localCacheManager = new LocalCacheManager(name, localConfig.getDefaultCache(), localCacheProvider,
                ShutdownManager.DEFAULT);
        var nearCacheManager = localCacheManager;

        return new MeshNetwork(config, nearCacheManager, localCacheManager, Transports.DEFAULT);
    }

    public MeshNetwork(MeshConfig config, LocalCacheManager nearCacheManager, LocalCacheManager localCacheManager,
            Transports transportRegistry) {

        this.config = config;
        this.route = new ConsistentHash<>(config.getHashing().instance);
        this.localCacheManager = localCacheManager;
        this.transportRegistry = transportRegistry;
        this.logger = LogHelper.getLogger(getClass(), config.getName());
        this.lifeStage = new LifeStage("meshnetwork", config.getName(), getLogger());
        this.meshCacheManager = new MeshCacheManager(nearCacheManager, this);

    }

    public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
        return getMeshCacheManager().resolveCache(cacheName, valueClass);
    }

    public void start() throws InterruptedException {
        getLifeStage().starting();

        for (var node : getRoute().nodes()) {
            node.start();
        }

        getLifeStage().started();
    }

    @Override
    public void shutdown(int timeoutSeconds) throws InterruptedException {
        getLifeStage().stopping();

        for (var node : getRoute().nodes()) {
            node.stop();
        }

        getLifeStage().stopped();
    }

    public MeshNode findNode(String key) {
        return getRoute().findNode(key);
    }

    public TransportProvider loadTransportProvider(String protocol) {
        var r = getTransportRegistry().getByKey(protocol);
        if (r == null) {
            throw new IllegalArgumentException("unsupported protocol: " + protocol);
        }
        return r;
    }

    // public MeshNode addLocalNode(String url) {
    // var nodeConfig = NodeConfig.fromUrl(url);
    // var pdr = loadTransportProvider(nodeConfig.getProtocol());
    // return addLocalNode(pdr, cm);
    // }

    protected MeshNode addLocalNode(NodeConfig nodeConfig, LocalTransport localTransport) {
        var pdr = loadTransportProvider(nodeConfig.getProtocol());
        var tp = new LocalTransport(getLocalCacheManager());

        if (pdr.bindLocalTransport(nodeConfig, tp) == false) {
            throw new IllegalArgumentException("transport " + pdr.getProtocol() + " doesn't support local node");
        }

        return addNode(pdr, nodeConfig, tp);
    }

    public MeshNode addRemoteNode(String url) {
        var nodeConfig = NodeConfig.fromUrl(url);
        var pdr = loadTransportProvider(nodeConfig.getProtocol());
        return addRemoteNode(pdr, nodeConfig);
    }

    public MeshNode addRemoteNode(NodeConfig nodeConfig) {
        var pdr = loadTransportProvider(nodeConfig.getProtocol());
        return addRemoteNode(pdr, nodeConfig);
    }

    protected MeshNode addRemoteNode(TransportProvider provider, NodeConfig nodeConfig) {
        var tp = provider.createRemoteTransport(nodeConfig);
        if (tp == null) {
            throw new IllegalArgumentException("transport " + provider.getProtocol() + " doesn't support remote node");
        }

        return addNode(provider, nodeConfig, tp);
    }

    protected MeshNode addNode(TransportProvider provider, NodeConfig nodeConfig, Transport transport) {
        var r = new MeshNode(nodeConfig, transport);
        r.addHook(provider);

        getRoute().join(r);
        return r;
    }

}
