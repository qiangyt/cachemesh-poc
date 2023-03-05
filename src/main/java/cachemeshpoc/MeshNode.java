package cachemeshpoc;

import java.net.URL;
import cachemeshpoc.util.ConsistentHash;

@lombok.Getter
public class MeshNode implements ConsistentHash.Node {

	private final boolean remote;
	private final URL url;
	private final String key;
	private final int hashCode;

	private final NodeCacheManager nodeCacheManager;

	public MeshNode(boolean remote, URL url, NodeCacheManager nodeCacheManager) {
		this.remote = remote;
		this.url = url;
		this.key = url.toExternalForm();
		this.hashCode = this.key.hashCode();
		this.nodeCacheManager = nodeCacheManager;
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
