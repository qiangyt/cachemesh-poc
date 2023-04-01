package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NodeCacheConfig implements Mappable {

	private final boolean cacheBytes;

	public NodeCacheConfig(boolean cacheBytes) {
		this.cacheBytes = cacheBytes;
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put( "cacheBytes", isCacheBytes());

		return r;
	}

}
