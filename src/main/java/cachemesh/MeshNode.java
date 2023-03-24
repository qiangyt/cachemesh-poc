package cachemesh;

import java.net.URL;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.NodeCache;

@lombok.Getter
public class MeshNode implements HasName, ConsistentHash.Node {

	protected final Logger logger = LogHelper.getLogger(this);

	private final boolean remote;
	private final URL url;
	private final String key;
	private final int hashCode;
	private final NodeCache nodeCache;

	public MeshNode(boolean remote, URL url, NodeCache nodeCache) {
		this.remote = remote;
		this.url = url;
		this.key = url.toExternalForm();
		this.hashCode = this.key.hashCode();
		this.nodeCache = nodeCache;
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		MeshNode that;
		try {
			that = (MeshNode)obj;
		} catch (ClassCastException e) {
			return false;
		}

		return this.key.equals(that.key);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return this.key + "@" + (this.remote ? "remote" : "local");
	}

}
