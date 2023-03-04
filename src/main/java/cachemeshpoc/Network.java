package cachemeshpoc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;

import cachemeshpoc.err.MeshInternalException;
import cachemeshpoc.json.JsonSerderializer;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCacheManager;
import cachemeshpoc.util.ConsistentHash;
import cachemeshpoc.util.MurmurHash;

@lombok.Getter
public class Network implements AutoCloseable {

	private final ConcurrentHashMap<String, Cache<?>> caches = new ConcurrentHashMap<>();

	private final LocalCacheManager nearCacheManager;

	private final ConsistentHash<Node> consistentHash;

	private final Map<String, Node> nodes = new HashMap<>();

	private final Serderializer serder;

	public Network(LocalCache.Factory nearCacheFactory) {
		this(new LocalCacheManager(nearCacheFactory), JsonSerderializer.DEFAULT);
	}

	public Network(LocalCacheManager nearCacheManager, Serderializer serder) {
		this.consistentHash = new ConsistentHash<>(MurmurHash.DEFAULT);
		this.nearCacheManager = nearCacheManager;
		this.serder = serder;
	}

	@SuppressWarnings("unchecked")
	public <T> Cache<T> getCache(String cacheName) {
		return (Cache<T>)this.caches.get(cacheName);
	}

	@SuppressWarnings("unchecked")
	public <T> Cache<T> resolveCache(String cacheName, Class<T> valueClass) {
		return (Cache<T>)this.caches.computeIfAbsent(cacheName, k -> {
			var nearCache = this.nearCacheManager.resolve(cacheName, VershedValue.class);
			return new Cache<T>(valueClass, nearCache, this, serder);
		});
	}

	public void addNodes(Iterable<Node> nodes) {
		nodes.forEach(this::addNode);
	}

	public void addNode(Node node) {
		if (this.nodes.putIfAbsent(node.getKey(), node) != null) {
			throw new MeshInternalException("duplicated node with key=%s", node.getKey());
		}

		this.consistentHash.join(node);
	}

	public Node findNode(String key) {
		long hash = this.consistentHash.hash(key);
		var virtualNode = this.consistentHash.virtualNodeFor(hash);
		return virtualNode.getRealNode();
	}

	@Override
	public void close() throws Exception {
		this.nearCacheManager.close();
	}

}
