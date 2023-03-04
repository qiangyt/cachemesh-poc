package cachemeshpoc.caffeine;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCache.Factory;

public class CaffeineCacheFactory implements Factory {

	@Override
	public <T> LocalCache<T> create(String cacheName, Class<T> valueClass) {
		var cfg = CaffeineCacheConfig.buildDefault();
		return new CaffeineCache<>(cacheName, valueClass, cfg);
	}

}
