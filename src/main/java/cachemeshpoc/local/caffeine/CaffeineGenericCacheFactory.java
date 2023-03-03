package cachemeshpoc.local.caffeine;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCache.Factory;

public class CaffeineGenericCacheFactory implements Factory {

	@Override
	public <T> LocalCache<T> create(String cacheName, Class<T> valueClass) {
		var cfg = CaffeineGenericCacheConfig.buildDefault();
		return new CaffeineGenericCache<>(cacheName, valueClass, cfg);
	}

}
