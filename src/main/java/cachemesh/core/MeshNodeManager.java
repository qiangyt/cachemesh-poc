package cachemesh.core;

import java.util.Map;

import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.hash.Hashing;
import cachemesh.core.local.LocalCacheManager;
import cachemesh.core.local.LocalNodeCache;
import cachemesh.spi.TransportProvider;
import lombok.Getter;

public class MeshNodeManager {

	private final ConsistentHash<MeshNode> route;

	@Getter
	private final LocalCacheManager localCacheManager;

	@Getter
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

	public void addNode(MeshNode node) {
		this.route.join(node);
	}

	public void addNodes(Iterable<MeshNode> nodes) {
		nodes.forEach(this::addNode);
	}

	public MeshNode addLocalNode(String url) {
		var transport  = Transport.parseUrl(url);
		var protocol = transport.getProtocol();

		var provider = getTransportRegistry().get(protocol);
		if (provider == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		var configMap = provider.parseUrl(url);

		return addLocalNode(provider, configMap);
	}

	public MeshNode addLocalNode(String protocol, Map<String, Object> configMap) {
		var provider = getTransportRegistry().get(protocol);
		if (provider == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		return addLocalNode(provider, configMap);
	}

	protected MeshNode addLocalNode(TransportProvider provider, Map<String, Object> configMap) {
		var localNodeCache = new LocalNodeCache(getLocalCacheManager());
		if (provider.setUpForLocalNode(configMap, localNodeCache) == false) {
			throw new IllegalArgumentException("provider " + provider.getName() + " doesn't support local node");
		}

		String target = Transport.getTarget(configMap);
		var node = new MeshNode(false, target, localNodeCache);
		addNode(node);
		return node;
	}


	public MeshNode addRemoteNode(String url) {
		var transport  = Transport.parseUrl(url);
		var protocol = transport.getProtocol();

		var provider = getTransportRegistry().get(protocol);
		if (provider == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		var configMap = provider.parseUrl(url);

		return addRemoteNode(provider, configMap);
	}

	public MeshNode addRemoteNode(String protocol, Map<String, Object> configMap) {
		var provider = getTransportRegistry().get(protocol);
		if (provider == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		return addRemoteNode(provider, configMap);
	}

	protected MeshNode addRemoteNode(TransportProvider provider, Map<String, Object> configMap) {
		var nodeCache = provider.setUpForRemoteNode(configMap);
		if (nodeCache == null) {
			throw new IllegalArgumentException("provider " + provider.getName() + " doesn't support remote node");
		}

		String target = Transport.getTarget(configMap);
		var node = new MeshNode(true, target, nodeCache);
		addNode(node);
		return node;
	}

}
