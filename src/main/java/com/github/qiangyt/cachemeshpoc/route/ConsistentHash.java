package com.github.qiangyt.cachemeshpoc.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.qiangyt.cachemeshpoc.util.Hashing;

// based on github.com/redis/jedis: redis.clients.jedis.providers.ShardedConnectionProvider
public class ConsistentHash {

	private final TreeMap<Long, Peer> nodes = new TreeMap<>();

	//private final Map<String, ConnectionPool> resources = new HashMap<>();

	private final Hashing algo;

	public ConsistentHash(List<Peer> shards, Hashing algo) {
		this.algo = algo;
		initialize(shards);
	}

	private void initialize(List<Peer> shards) {
		for (int i = 0; i < shards.size(); i++) {
			Peer shard = shards.get(i);
			for (int n = 0; n < 160; n++) {
				Long hash = this.algo.hash("SHARD-" + i + "-NODE-" + n);
				nodes.put(hash, shard);
				//setupNodeIfNotExist(shard);
			}
		}
	}

	/*private ConnectionPool setupNodeIfNotExist(final HostAndPort node) {
		String nodeKey = node.toString();
		ConnectionPool existingPool = resources.get(nodeKey);
		if (existingPool != null)
			return existingPool;

		ConnectionPool nodePool = poolConfig == null ? new ConnectionPool(node, clientConfig)
				: new ConnectionPool(node, clientConfig, poolConfig);
		resources.put(nodeKey, nodePool);
		return nodePool;
	}*/

	public Hashing getHashingAlgo() {
		return algo;
	}

	public Peer getNode(Long hash) {
		return hash != null ? getNodeFromHash(hash) : null;
	}

	private Peer getNodeFromHash(Long hash) {
		SortedMap<Long, Peer> tail = nodes.tailMap(hash);
		if (tail.isEmpty()) {
			return nodes.get(nodes.firstKey());
		}
		return tail.get(tail.firstKey());
	}

}
