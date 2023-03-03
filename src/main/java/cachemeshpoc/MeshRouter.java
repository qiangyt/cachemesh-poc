package cachemeshpoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cachemeshpoc.util.ConsistentHash;
import cachemeshpoc.util.MurmurHash;

public class MeshRouter implements AutoCloseable {

	private final ConsistentHash<MeshNode> consistentHash;

	private final Map<String, MeshNode> nodes = new HashMap<>();

	public MeshRouter(List<MeshNode> otherNodes) {
		otherNodes.forEach(this::joinNode);

		this.consistentHash = new ConsistentHash<>(MurmurHash.DEFAULT);
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

	@Override
	public void close() throws Exception {
		// this.remote.close();
		// this.local.close();
	}

}
