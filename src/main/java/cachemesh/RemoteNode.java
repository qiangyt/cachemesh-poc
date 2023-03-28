package cachemesh;

import java.net.URL;

import cachemesh.spi.NodeCache;

@lombok.Getter
public class RemoteNode extends MeshNode {

	private final NodeCache nodeCache;

	public RemoteNode(URL url, NodeCache nodeCache) {
		super(url);
		this.nodeCache = nodeCache;
	}

	public boolean isRemote() {
		return true;
	}

}
