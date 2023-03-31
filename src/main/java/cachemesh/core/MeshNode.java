package cachemesh.core;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.NodeCache;

@lombok.Getter
public class MeshNode implements HasName, ConsistentHash.Node {

	protected final Logger logger;

	private final String key;
	private final int hashCode;
	private final boolean remote;
	private final NodeCache cache;

	public MeshNode(boolean remote, String key, NodeCache cache) {
		this.remote = remote;
		this.cache = cache;

		this.key = key;
		this.hashCode = this.key.hashCode();
		this.logger = LogHelper.getLogger(this);
	}

	public void shutdown(int timeoutSeconds) throws InterruptedException {
		getCache().shutdown(timeoutSeconds);
	}

	@Override
	public String getName() {
		return getKey();
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
		return this.key + "@" + (isRemote() ? "remote" : "local");
	}

}
