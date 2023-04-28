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
package cachemesh.core.cache;

import java.net.MalformedURLException;

import org.slf4j.Logger;

import lombok.Getter;
import cachemesh.common.err.BadValueException;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.misc.LogHelper;
import cachemesh.common.misc.SimpleURL;
import cachemesh.core.MeshNode;
import cachemesh.core.cache.local.LocalCacheManager;
import cachemesh.core.cache.local.LocalTransport;
import cachemesh.core.cache.spi.TransportProvider;
import cachemesh.core.cache.transport.Transport;
import cachemesh.core.config.MeshConfigService;
import cachemesh.core.config.NodeConfig;
import lombok.AccessLevel;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class MeshCacheService {

    @Nonnull
    private final Logger logger;

    @Nonnull
    private final MeshConfigService configService;

    @Nonnull
    @Getter(AccessLevel.PROTECTED)
    private final ConsistentHash<MeshNode> route;

    @Nonnull
    private final LocalCacheManager localCacheManager;

    @Nonnull
    private final LocalCacheManager nearCacheManager;

    @Nonnull
    private final MeshCacheManager meshCacheManager;

    public MeshCacheService(@Nonnull MeshConfigService configService) {
        checkNotNull(configService);

        var cfg = configService.getConfig();
        var name = cfg.getName();

        this.logger = LogHelper.getLogger(getClass(), name);
        this.configService = configService;
        this.route = new ConsistentHash<>(cfg.getHashing().instance);

        this.localCacheManager = configService.createLocalCacheManager();
        this.nearCacheManager = this.localCacheManager;
        this.meshCacheManager = null;// new MeshCacheManager(nearCacheManager, this);
    }

    @Nonnull
    public <T> MeshCache<T> resolveCache(@Nonnull String cacheName, @Nonnull Class<T> valueClass) {
        checkNotNull(cacheName);
        checkNotNull(valueClass);

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

    @Nonnull
    public MeshNode findNode(@Nonnull String key) {
        checkNotNull(key);

        return getRoute().findNode(key);
    }

    @Nonnull
    public TransportProvider loadTransportProvider(@Nonnull String protocol) {
        checkNotNull(protocol);

        return getConfigService().getTransportRegistry().load(protocol);
    }

    @Nonnull
    public MeshNode addLocalNode(@Nonnull String url) throws MalformedURLException {
        checkNotNull(url);

        var kind = new SimpleURL(url).getProtocol();
        NodeConfig nodeConfig = null;// NodeConfig.fromUrl(url);
        var pdr = loadTransportProvider(kind);
        return null;// addLocalNode(nodeConfig, pdr);
    }

    @Nonnull
    protected MeshNode addLocalNode(@Nonnull NodeConfig nodeConfig, @Nonnull LocalTransport localTransport) {
        checkNotNull(nodeConfig);
        checkNotNull(localTransport);

        var pdr = loadTransportProvider(nodeConfig.getProtocol());
        var tp = new LocalTransport(getLocalCacheManager());

        if (pdr.bindLocalTransport(nodeConfig, tp) == false) {
            throw new BadValueException("transport %a doesn't support local node", nodeConfig.getProtocol());
        }

        return addNode(pdr, nodeConfig, tp);
    }

    @Nonnull
    public MeshNode addRemoteNode(@Nonnull String url) throws MalformedURLException {
        checkNotNull(url);

        var kind = new SimpleURL(url).getProtocol();
        NodeConfig nodeConfig = null;// NodeConfig.fromUrl(url);
        var pdr = loadTransportProvider(kind);
        return addRemoteNode(pdr, nodeConfig);
    }

    @Nonnull
    public MeshNode addRemoteNode(@Nonnull NodeConfig nodeConfig) {
        checkNotNull(nodeConfig);

        var pdr = loadTransportProvider(nodeConfig.getProtocol());
        return addRemoteNode(pdr, nodeConfig);
    }

    @Nonnull
    protected MeshNode addRemoteNode(@Nonnull TransportProvider provider, @Nonnull NodeConfig nodeConfig) {
        checkNotNull(provider);
        checkNotNull(nodeConfig);

        var tp = provider.createRemoteTransport(nodeConfig);
        if (tp == null) {
            throw new BadValueException("transport %a doesn't support remote node", nodeConfig.getProtocol());
        }

        return addNode(provider, nodeConfig, tp);
    }

    @Nonnull
    protected MeshNode addNode(@Nonnull TransportProvider provider, @Nonnull NodeConfig nodeConfig,
            @Nonnull Transport transport) {
        checkNotNull(provider);
        checkNotNull(nodeConfig);
        checkNotNull(transport);

        var r = new MeshNode(nodeConfig, transport);
        r.addHook(provider);

        getRoute().join(r);
        return r;
    }

}
