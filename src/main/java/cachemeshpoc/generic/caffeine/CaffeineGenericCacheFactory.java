package cachemeshpoc.generic.caffeine;

import cachemeshpoc.generic.GenericCache;
import cachemeshpoc.generic.GenericCache.Factory;

public class CaffeineGenericCacheFactory implements Factory {

	@Override
	public <T> GenericCache<T> create(String cacheName, Class<T> valueClass) {
		var cfg = CaffeineGenericCacheConfig.buildDefault();
		return new CaffeineGenericCache<>(cacheName, valueClass, cfg);
	}

}
