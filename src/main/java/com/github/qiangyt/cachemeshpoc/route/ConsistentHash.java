package com.github.qiangyt.cachemeshpoc.route;

import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.qiangyt.cachemeshpoc.util.Hashing;

// based on github.com/redis/jedis: redis.clients.jedis.providers.ShardedConnectionProvider
public class ConsistentHash {

	private static final Logger LOG = LoggerFactory.getLogger(ConsistentHash.class);

	private final TreeMap<Long, Node> nodes = new TreeMap<>();

	private final Hashing algo;

	public ConsistentHash(List<Node> nodes, Hashing algo) {
		this.algo = algo;
		initialize(nodes);
	}

	private void initialize(List<Node> nodes) {
		for (var node: nodes) {
			join(node);
		}
	}

	public void join(Node node) {
		for (int n = 0; n < 160; n++) {
			String key = String.format("node-%d-%s", this.nodes.size(), n);
			long hash = hash(key);
			nodes.put(hash, node);
		}
	}

	public long hash(String key) {
		return this.algo.hash(key);
	}

	public Hashing getHashingAlgo() {
		return this.algo;
	}

	public Node getNode(String key) {
		long hash = this.hash(key);
		return key != null ? getNode(hash) : null;
	}

	private Node getNode(long hash) {
		var nodes = this.nodes;
		var tail = nodes.tailMap(hash);
		if (tail.isEmpty()) {
			return nodes.get(nodes.firstKey());
		}
		return tail.get(tail.firstKey());
	}

}
