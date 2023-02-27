package com.github.qiangyt.cachemeshpoc.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.qiangyt.cachemeshpoc.LocalCache;
import com.github.qiangyt.cachemeshpoc.MeshCache;
import com.github.qiangyt.cachemeshpoc.RemoteCache;
import com.github.qiangyt.cachemeshpoc.caffeine.CaffeineLocalCacheBuilder;
import com.github.qiangyt.cachemeshpoc.route.ConsistentHash;
import com.github.qiangyt.cachemeshpoc.route.Node;
import com.github.qiangyt.cachemeshpoc.util.Hashing;
import com.github.qiangyt.cachemeshpoc.util.MurmurHash;

public class DefaultMeshCache<V> extends AbstractCache<V> {

	private final LocalCache<V> local;

	private final ConsistentHash consistentHash;
	private final Node selfNode;

	public DefaultMeshCache(String name, Class<V> valueClass, Node selfNode, List<Node> otherNodes) {
		super(name, valueClass);
		this.local = new CaffeineLocalCacheBuilder().build(name, valueClass);

		var nodes = new ArrayList<Node>(otherNodes);
		nodes.add(selfNode);
		this.consistentHash = new ConsistentHash(nodes, MurmurHash.DEFAULT);

		this.selfNode = selfNode;
	}

	@Override
	public V getSingle(String key) {
		var localEntry = this.local.getSingle(key);

		long hash = this.consistentHash.hash(key);
		var virtualNode = this.consistentHash.virtualNode(hash);
		var node = virtualNode.getRealNode();

		if (this.selfNode == node) {
			if (localEntry == null) {
				return null;
			}
			return localEntry.value();
		}

		long version;
		if (localEntry == null) {
			version = 0;
		} else {
			version = localEntry.version();
		}

		var remote = node.getRemoteCache();
		var resp = remote.getSingle(key, version);

		switch(resp.status()) {
			case Changed: {
				return localEntry.value();
			}
			case NoChange: {
				return localEntry.value();
			}
			case NotFound: {
				return null;
			}
			default: {
				throw new IllegalStateException("unexpected status: " + resp.status());
			}
		}
	}

	@Override
	public void setSingle(String key, V value) {

	}

	@Override
	public void close() throws Exception {
		this.remote.close();
		this.local.close();
	}

}
