package cachemeshpoc.util;

import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// originally, it is based on github.com/redis/jedis: redis.clients.jedis.providers.ShardedConnectionProvider
// TODO: another choice is akka.routing.ConsistentHash
public class ConsistentHash<T extends ConsistentHash.Node> {

	public static interface Node {
		String getKey();
	}

	@lombok.Getter
	public static class VirtualNode <T extends Node>{

		private final T realNode;
		private final int index;
		private final String key;
		private final long hash;

		public VirtualNode(T realNode, int index, String key, long hash) {
			this.realNode = realNode;
			this.index = index;
			this.key = key;
			this.hash = hash;
		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(ConsistentHash.class);

	private final TreeMap<Long, VirtualNode<T>> ring = new TreeMap<>();

	private final Hashing algo;

	public ConsistentHash(Hashing algo) {
		this.algo = algo;
	}

	public void join(List<T> nodes) {
		LOG.info("got {} nodes to join", nodes.size());
		nodes.forEach(this::join);
	}

	public void join(T node) {
		LOG.info("node {} is joining", node);

		boolean debug = LOG.isDebugEnabled();
		var rg = this.ring;
		String nodeKey = node.getKey();

		for (int i = 0; i < 160; i++) {
			String virtualNodeKey = String.format("%s-%d", nodeKey, i);
			long h = hash(virtualNodeKey);
			VirtualNode<T> virtualNode = new VirtualNode<>(node, i, virtualNodeKey, h);

			if (debug) {
				LOG.debug("node {} virtual nodes {}: key={}, hash={}", node, i, virtualNodeKey, h);
			}
			rg.put(h, virtualNode);
		}
	}

	public long hash(String key) {
		return this.algo.hash(key);
	}

	public Hashing algo() {
		return this.algo;
	}

	public VirtualNode<T> virtualNodeFor(String key) {
		long h = hash(key);
		return key != null ? virtualNodeFor(h) : null;
	}

	public VirtualNode<T> virtualNodeFor(long hash) {
		var r = this.ring;
		var tail = r.tailMap(hash);
		if (tail.isEmpty()) {
			return r.get(r.firstKey());
		}
		return tail.get(tail.firstKey());
	}

	public T nodeFor(String key) {
		return virtualNodeFor(key).getRealNode();
	}

}
