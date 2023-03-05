package cachemeshpoc.caffeine;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCacheFactory;

public class CaffeineCacheFactory implements LocalCacheFactory {

	@Override
	public <T> LocalCache<T> create(String cacheName, Class<T> valueClass) {
		var cfg = CaffeineCacheConfig.defaultConfig();
		return new CaffeineCache<>(cacheName, valueClass, cfg);
	}

}
