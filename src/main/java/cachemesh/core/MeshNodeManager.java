package cachemesh.core;

import java.util.Map;

import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.hash.Hashing;
import cachemesh.spi.Transport;
import lombok.Getter;

@Getter
public class MeshNodeManager {

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

	public void addNode(MeshNode node) {
		this.route.join(node);
	}

	public void addNodes(Iterable<MeshNode> nodes) {
		nodes.forEach(this::addNode);
	}

	public MeshNode addLocalNode(String url) {
		var protocol = TransportURL.parseUrl(url).getProtocol();

		var transport = getTransportRegistry().get(protocol);
		if (transport == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		var configMap = transport.parseUrl(url);

		return addLocalNode(transport, configMap);
	}

	public MeshNode addLocalNode(String protocol, Map<String, Object> configMap) {
		var transport = getTransportRegistry().get(protocol);
		if (transport == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		return addLocalNode(transport, configMap);
	}

	protected MeshNode addLocalNode(Transport transport, Map<String, Object> configMap) {
		var localNodeCache = new LocalNodeCache(getLocalCacheManager());
		if (transport.setUpForLocalNode(configMap, localNodeCache) == false) {
			throw new IllegalArgumentException("transport " + transport.getProtocol() + " doesn't support local node");
		}

		String target = TransportURL.getTarget(configMap);
		var node = new MeshNode(false, target, localNodeCache);
		addNode(node);
		return node;
	}


	public MeshNode addRemoteNode(String url) {
		var protocol = TransportURL.parseUrl(url).getProtocol();

		var transport = getTransportRegistry().get(protocol);
		if (transport == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		var configMap = transport.parseUrl(url);

		return addRemoteNode(transport, configMap);
	}

	public MeshNode addRemoteNode(String protocol, Map<String, Object> configMap) {
		var transport = getTransportRegistry().get(protocol);
		if (transport == null) {
			throw new IllegalArgumentException("unsupported protocol: " + protocol);
		}

		return addRemoteNode(transport, configMap);
	}

	protected MeshNode addRemoteNode(Transport transport, Map<String, Object> configMap) {
		var nodeCache = transport.createRemoteCache(configMap);
		if (nodeCache == null) {
			throw new IllegalArgumentException("transport " + transport.getProtocol() + " doesn't support remote node");
		}

		String target = TransportURL.getTarget(configMap);
		var node = new MeshNode(true, target, nodeCache);
		addNode(node);
		return node;
	}

}
