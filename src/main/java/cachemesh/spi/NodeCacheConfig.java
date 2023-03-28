package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;

@lombok.Getter
@lombok.experimental.SuperBuilder
public class NodeCacheConfig implements HasName {

	private final String name;

	private final boolean cacheBytes;

	public NodeCacheConfig(String name, boolean cacheBytes) {
		this.name = name;
		this.cacheBytes = cacheBytes;
	}

	public NodeCacheConfig buildAnother(String name) {
		return new NodeCacheConfig(name, isCacheBytes());
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put( "cacheBytes", isCacheBytes());

		return r;
	}

}
