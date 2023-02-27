package com.github.qiangyt.cachemeshpoc.route;

@lombok.Getter
public class VirtualNode {

	private final Node realNode;
	private final int index;
	private final String key;
	private final long hash;

	public VirtualNode(Node realNode, int index, String key, long hash) {
		this.realNode = realNode;
		this.index = index;
		this.key = key;
		this.hash = hash;
	}

}
