package cachemesh.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import cachemesh.spi.LocalCacheFactory;
import cachemesh.spi.Value;

public class CaffeineFactory implements LocalCacheFactory {

	public static final CaffeineFactory DEFAULT = new CaffeineFactory();

	@Override
	public LocalCache create(LocalCacheConfig config) {
		CaffeineConfig cconfig = (CaffeineConfig)config;
		Cache<String, Value> instance = Caffeine.newBuilder()
									.maximumSize(cconfig.getMaximumSize())
									.expireAfterWrite(cconfig.getExpireAfterWrite())
									.build();
		return new CaffeineCache(cconfig, instance);
	}

	@Override
	public String getName() {
		return "caffeine";
	}

}
