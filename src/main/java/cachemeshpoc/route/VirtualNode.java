package cachemeshpoc.route;

import cachemeshpoc.MeshAddress;

@lombok.Getter
public class VirtualNode {

	private final MeshAddress realNode;
	private final int index;
	private final String key;
	private final long hash;

	public VirtualNode(MeshAddress realNode, int index, String key, long hash) {
		this.realNode = realNode;
		this.index = index;
		this.key = key;
		this.hash = hash;
	}

}
