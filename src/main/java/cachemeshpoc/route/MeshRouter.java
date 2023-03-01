package cachemeshpoc.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cachemeshpoc.MeshAddress;
import cachemeshpoc.hash.MurmurHash;

public class MeshRouter implements AutoCloseable {

	private final ConsistentHash consistentHash;

	private final MeshAddress selfNode;

	private final Map<String, MeshAddress> nodes = new HashMap<>();

	public MeshRouter(MeshAddress selfNode, List<MeshAddress> otherNodes) {
		var nodes = new ArrayList<MeshAddress>(otherNodes);
		nodes.add(selfNode);
		this.consistentHash = new ConsistentHash(MurmurHash.DEFAULT);

		this.selfNode = selfNode;
	}

	public void joinNode(MeshAddress newNode) {
		/*
		 * if (this.nodes.containsKey(newNode.getKey()) {
		 *
		 * }
		 */
		this.consistentHash.join(newNode);
	}

	public MeshAddress findNode(String key) {
		long hash = this.consistentHash.hash(key);
		var virtualNode = this.consistentHash.virtualNode(hash);
		return virtualNode.getRealNode();
	}

	public boolean isSelfNode(MeshAddress node) {
		return (this.selfNode == node);
	}

	@Override
	public void close() throws Exception {
		// this.remote.close();
		// this.local.close();
	}

}
