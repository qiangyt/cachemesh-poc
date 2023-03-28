package cachemesh;

import java.net.URL;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.util.LogHelper;

@lombok.Getter
public abstract class MeshNode implements HasName, ConsistentHash.Node {

	protected final Logger logger;

	private final URL url;
	private final String key;
	private final int hashCode;

	public MeshNode(URL url) {
		this.url = url;
		this.key = url.toExternalForm();
		this.hashCode = this.key.hashCode();
		this.logger = LogHelper.getLogger(this);
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

	public abstract boolean isRemote();

	@Override
	public String toString() {
		return this.key + "@" + (isRemote() ? "remote" : "local");
	}

}
