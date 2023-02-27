package com.github.qiangyt.cachemeshpoc.route;

import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.qiangyt.cachemeshpoc.util.Hashing;

// based on github.com/redis/jedis: redis.clients.jedis.providers.ShardedConnectionProvider
public class ConsistentHash {

	private static final Logger LOG = LoggerFactory.getLogger(ConsistentHash.class);

	private final TreeMap<Long, VirtualNode> ring = new TreeMap<>();

	private final List<Node> nodes;

	private final Hashing algo;

	public ConsistentHash(List<Node> nodes, Hashing algo) {
		this.nodes = nodes;
		this.algo = algo;
		join(nodes);
	}

	public List<Node> nodes() {
		return this.nodes;
	}

	public void join(List<Node> nodes) {
		LOG.info("got {} nodes to join", nodes.size());

		for (var node: nodes) {
			join(node);
		}
	}

	public void join(Node node) {
		LOG.info("node {} is joining", node);

		boolean debug = LOG.isDebugEnabled();
		var rg = this.ring;
		String nodeKey = node.getKey();

		for (int i = 0; i < 160; i++) {
			String virtualNodeKey = String.format("%s-%d", nodeKey, i);
			Long h = hash(virtualNodeKey);
			var virtualNode = new VirtualNode(node, i, virtualNodeKey, h);

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

	public VirtualNode virtualNode(String key) {
		long h = hash(key);
		return key != null ? virtualNode(h) : null;
	}

	public VirtualNode virtualNode(long hash) {
		var r = this.ring;
		var tail = r.tailMap(hash);
		if (tail.isEmpty()) {
			return r.get(r.firstKey());
		}
		return tail.get(tail.firstKey());
	}

}
