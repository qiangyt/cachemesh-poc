package cachemeshpoc.local.caffeine;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCache.Factory;

public class CaffeineLocalCacheFactory implements Factory {

	@Override
	public <T> LocalCache<T> create(String cacheName, Class<T> valueClass) {
		var cfg = CaffeineLocalCacheConfig.buildDefault();
		return new CaffeineLocalCache<>(cacheName, valueClass, cfg);
	}

}
