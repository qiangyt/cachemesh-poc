package cachemesh.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.spi.LocalCacheFactory;
import cachemesh.spi.base.Value;

public class CaffeineFactory<T, V extends Value<T>> implements LocalCacheFactory<T, V, CaffeineConfig<T>, CaffeineCache<T, V>> {

	public static final CaffeineFactory DEFAULT = new CaffeineFactory<>();

	@Override
	public CaffeineCache<T,V> create(CaffeineConfig<T> config) {
		Cache<String, V> instance = Caffeine.newBuilder()
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
