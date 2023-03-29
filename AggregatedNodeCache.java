package cachemesh.spi;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.spi.base.GetResult;

@ThreadSafe
public class AggregatedNodeCache {

	private NodeCacheManager cacheManager;

	public AggregatedNodeCache(NodeCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var cache = this.cacheManager.get(cacheName);
		if (cache == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}
		return cache.getSingle(key, version);
	}

	// return version
	public long putSingle(String cacheName, String key, byte[] value) {
		var cache = this.cacheManager.resolve(cacheName);
		return cache.putSingle(key, value);
	}


}
