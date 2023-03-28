package cachemesh;

import java.net.URL;

import cachemesh.spi.NodeCache;

@lombok.Getter
public class LocalNode extends MeshNode {

	private final NodeCache nodeCache;

	public LocalNode(URL url, NodeCache nodeCache) {
		super(url);
		this.nodeCache = nodeCache;
	}

	public boolean isRemote() {
		return false;
	}

}
