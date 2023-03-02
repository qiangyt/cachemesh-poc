package cachemeshpoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cachemeshpoc.util.ConsistentHash;
import cachemeshpoc.util.MurmurHash;

public class MeshRouter implements AutoCloseable {

	private final ConsistentHash<MeshNode> consistentHash;

	private final MeshNode myNode;

	private final Map<String, MeshNode> nodes = new HashMap<>();

	public MeshRouter(MeshNode myNode, List<MeshNode> otherNodes) {
		otherNodes.forEach(this::joinNode);
		joinNode(myNode);

		this.consistentHash = new ConsistentHash<>(MurmurHash.DEFAULT);

		this.myNode = myNode;
	}

	public void joinNode(MeshNode newNode) {
		this.consistentHash.join(newNode);
		this.nodes.put(newNode.getKey(), newNode);
	}

	public MeshNode findNode(String key) {
		long hash = this.consistentHash.hash(key);
		var virtualNode = this.consistentHash.virtualNodeFor(hash);
		return virtualNode.getRealNode();
	}

	public boolean isMyNode(MeshNode node) {
		return (this.myNode == node);
	}

	@Override
	public void close() throws Exception {
		// this.remote.close();
		// this.local.close();
	}

}
