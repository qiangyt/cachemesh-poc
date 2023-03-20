package cachemesh.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import cachemesh.spi.base.Value;

public class CaffeineCacheFactory<T> implements LocalCache.Factory<T> {

	public static final CaffeineCacheFactory<Object> DEFAULT = new CaffeineCacheFactory<>();

	@Override
	public LocalCache<T> create(LocalCacheConfig<T> config) {
		return build((CaffeineCacheConfig<T>)config);
	}

	public CaffeineCache<T> build(CaffeineCacheConfig<T> config) {
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
