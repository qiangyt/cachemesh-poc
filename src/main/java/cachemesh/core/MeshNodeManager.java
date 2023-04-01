package cachemesh.core;

import java.util.Map;

import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.hash.Hashing;
import cachemesh.spi.Transport;
import cachemesh.spi.TransportProvider;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class MeshNodeManager {

	@Getter(AccessLevel.PROTECTED)
	private final ConsistentHash<MeshNode> route;

	private final LocalCacheManager localCacheManager;

	private final TransportRegistry transportRegistry;

	public MeshNodeManager(Hashing hashing,
						   LocalCacheManager localCacheManager,
						   TransportRegistry transportRegistry) {
		this.route = new ConsistentHash<>(hashing);
		this.localCacheManager = localCacheManager;
		this.transportRegistry = transportRegistry;
	}

	public MeshNode findNode(String key) {
		return this.route.findNode(key);
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
