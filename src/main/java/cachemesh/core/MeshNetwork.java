package cachemesh.core;

import java.util.Map;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.LifeStage;
import cachemesh.common.shutdown.Shutdownable;
import cachemesh.common.util.LogHelper;
import cachemesh.grpc.GrpcTransportProvider;
import cachemesh.lettuce.LettuceTransportProvider;
import lombok.Getter;


import cachemesh.common.hash.ConsistentHash;
import cachemesh.spi.Transport;
import cachemesh.spi.TransportProvider;
import lombok.AccessLevel;



@Getter
public class MeshNetwork implements Shutdownable, HasName {

	static {
		GrpcTransportProvider.register();
		LettuceTransportProvider.register();
	}

	private final MeshNetworkConfig config;

	@Getter(AccessLevel.PROTECTED)
	private final ConsistentHash<MeshNode> route;

	private final LocalCacheManager localCacheManager;

	private final TransportRegistry transportRegistry;

	private final Logger logger;

	private final LifeStage lifeStage;

	private final MeshCacheManager meshCacheManager;

	public MeshNetwork(MeshNetworkConfig config,
					   LocalCacheManager nearCacheManager,
					   LocalCacheManager localCacheManager,
					   TransportRegistry transportRegistry) {

		this.config = config;
		this.route = new ConsistentHash<>(config.getHashing());
		this.localCacheManager = localCacheManager;
		this.transportRegistry = transportRegistry;
		this.logger = LogHelper.getLogger(this);
		this.lifeStage = new LifeStage("meshnetwork", config.getName(), getLogger());
		this.meshCacheManager = new MeshCacheManager(nearCacheManager, this);

	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

	public void start() throws InterruptedException {
		getLifeStage().starting();

		for (var node: getRoute().nodes()) {
			node.start();
		}

		getLifeStage().started();
	}

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		getLifeStage().stopping();

		for (var node: getRoute().nodes()) {
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

	public MeshNode addLocalNode(String url) {
		var pc = TransportURL.parseUrl(url).getProtocol();
		var pdr = loadTransportProvider(pc);
		var cm = pdr.parseUrl(url);

		return addLocalNode(pdr, cm);
	}

	public MeshNode addLocalNode(Map<String, Object> configMap) {
		var pc = (String)configMap.get("protocol");
		var pdr = loadTransportProvider(pc);

		return addLocalNode(pdr, configMap);
	}

	protected MeshNode addLocalNode(TransportProvider provider, Map<String, Object> configMap) {
		var tp = new LocalTransport(getLocalCacheManager());
		var cfg = provider.parseConfig(configMap);

		if (provider.setUpLocalTransport(cfg, tp) == false) {
			throw new IllegalArgumentException("transport " + provider.getProtocol() + " doesn't support local node");
		}

		return addNode(provider, cfg, tp);
	}

	public MeshNode addRemoteNode(String url) {
		var pc = TransportURL.parseUrl(url).getProtocol();
		var pdr = loadTransportProvider(pc);
		var cm = pdr.parseUrl(url);

		return addRemoteNode(pdr, cm);
	}

	public MeshNode addRemoteNode(Map<String, Object> configMap) {
		var pc = (String)configMap.get("protocol");
		var pdr = loadTransportProvider(pc);

		return addRemoteNode(pdr, configMap);
	}

	protected MeshNode addRemoteNode(TransportProvider provider, Map<String, Object> configMap) {
		var cfg = provider.parseConfig(configMap);

		var tp = provider.createRemoteTransport(cfg);
		if (tp == null) {
			throw new IllegalArgumentException("transport " + provider.getProtocol() + " doesn't support remote node");
		}

		return addNode(provider, cfg, tp);
	}

	protected MeshNode addNode(TransportProvider provider, TransportConfig config, Transport transport) {
		var r = new MeshNode(config, transport);
		r.addHook(provider);

		getRoute().join(r);
		return r;
	}

}
