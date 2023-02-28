package qiangyt.cachemeshpoc.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qiangyt.cachemeshpoc.hash.MurmurHash;

public class MeshRouter implements AutoCloseable {

	private final ConsistentHash consistentHash;

	private final MeshNode selfNode;

	private final Map<String, MeshNode> nodes = new HashMap<>();

	public MeshRouter(MeshNode selfNode, List<MeshNode> otherNodes) {
		var nodes = new ArrayList<MeshNode>(otherNodes);
		nodes.add(selfNode);
		this.consistentHash = new ConsistentHash(MurmurHash.DEFAULT);

		this.selfNode = selfNode;
	}

	public void joinNode(MeshNode newNode) {
		/*
		 * if (this.nodes.containsKey(newNode.getKey()) {
		 *
		 * }
		 */
		this.consistentHash.join(newNode);
	}

	public MeshNode findNode(String key) {
		long hash = this.consistentHash.hash(key);
		var virtualNode = this.consistentHash.virtualNode(hash);
		return virtualNode.getRealNode();
	}

	public boolean isSelfNode(MeshNode node) {
		return (this.selfNode == node);
	}

	@Override
	public void close() throws Exception {
		// this.remote.close();
		// this.local.close();
	}

}
