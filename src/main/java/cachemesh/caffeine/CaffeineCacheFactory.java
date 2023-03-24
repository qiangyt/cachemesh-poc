package cachemesh.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.spi.CommonCacheFactory;
import cachemesh.spi.base.Value;

public class CaffeineCacheFactory<T> implements CommonCacheFactory<T, CaffeineCacheConfig<T>, CaffeineCache<T>> {

	public static final CaffeineCacheFactory<Object> DEFAULT = new CaffeineCacheFactory<>();

	@Override
	public CaffeineCache<T> create(CaffeineCacheConfig<T> config) {
		Cache<String, Value<T>> instance = Caffeine.newBuilder()
												.maximumSize(config.getMaximumSize())
												.expireAfterWrite(config.getExpireAfterWrite())
												.build();
		return new CaffeineCache<>(config, instance);
	}

	@Override
	public String getName() {
		return "caffeine";
	}

}
