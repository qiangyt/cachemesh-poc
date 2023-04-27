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

import java.net.MalformedURLException;

import org.slf4j.Logger;

import lombok.Getter;

import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.misc.LogHelper;
import cachemesh.common.misc.SimpleURL;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.Transport;
import cachemesh.core.spi.TransportProvider;
import lombok.AccessLevel;

@Getter
public class MeshCacheService {

    private final Logger logger;

    private final MeshConfigService configService;

    @Getter(AccessLevel.PROTECTED)
    private final ConsistentHash<MeshNode> route;

    private final LocalCacheManager localCacheManager;

    private final LocalCacheManager nearCacheManager;

    private final MeshCacheManager meshCacheManager;

    public MeshCacheService(MeshConfigService configService) {

        var cfg = configService.getConfig();
        var name = cfg.getName();
        
        this.logger = LogHelper.getLogger(getClass(), name);
        this.configService = configService;
        this.route = new ConsistentHash<>(cfg.getHashing().instance);
        
        this.localCacheManager = configService.createLocalCacheManager();
        this.nearCacheManager = this.localCacheManager;
        this.meshCacheManager = null;//new MeshCacheManager(nearCacheManager, this);
    }

    public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
        return getMeshCacheManager().resolveCache(cacheName, valueClass);
    }

    public void start() throws InterruptedException {
        for (var node : getRoute().nodes()) {
            node.start();
        }
    }

    public void stop() throws InterruptedException {
        for (var node : getRoute().nodes()) {
            node.stop();
        }
    }

    public MeshNode findNode(String key) {
        return getRoute().findNode(key);
    }

    public TransportProvider loadTransportProvider(String protocol) {
        var r = getConfigService().getTransportRegistry().get(protocol);
        if (r == null) {
            throw new IllegalArgumentException("unsupported protocol: " + protocol);
        }
        return r;
    }

    public MeshNode addLocalNode(String url) throws MalformedURLException {
        var kind = new SimpleURL(url).getProtocol();
        NodeConfig nodeConfig = null;//NodeConfig.fromUrl(url);
        var pdr = loadTransportProvider(kind);
        return null;//addLocalNode(nodeConfig, pdr);
    }

    protected MeshNode addLocalNode(NodeConfig nodeConfig, LocalTransport localTransport) {
        var pdr = loadTransportProvider(nodeConfig.getProtocol());
        var tp = new LocalTransport(getLocalCacheManager());

        if (pdr.bindLocalTransport(nodeConfig, tp) == false) {
            throw new IllegalArgumentException("transport " + nodeConfig.getProtocol() + " doesn't support local node");
        }

        return addNode(pdr, nodeConfig, tp);
    }

    public MeshNode addRemoteNode(String url) throws MalformedURLException {
        var kind = new SimpleURL(url).getProtocol();
        NodeConfig nodeConfig = null;//NodeConfig.fromUrl(url);
        var pdr = loadTransportProvider(kind);
        return addRemoteNode(pdr, nodeConfig);
    }

    public MeshNode addRemoteNode(NodeConfig nodeConfig) {
        var pdr = loadTransportProvider(nodeConfig.getProtocol());
        return addRemoteNode(pdr, nodeConfig);
    }

    protected MeshNode addRemoteNode(TransportProvider provider, NodeConfig nodeConfig) {
        var tp = provider.createRemoteTransport(nodeConfig);
        if (tp == null) {
            throw new IllegalArgumentException(
                    "transport " + nodeConfig.getProtocol() + " doesn't support remote node");
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
