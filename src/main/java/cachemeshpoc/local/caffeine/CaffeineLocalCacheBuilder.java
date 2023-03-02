package cachemeshpoc.local.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCache.Builder;
import cachemeshpoc.local.Entry.Value;

public class CaffeineLocalCacheBuilder implements Builder {

	@Override
	public <T> LocalCache<T> build(String cacheName) {
		var config = CaffeineLocalCacheConfig.buildDefault(cacheName);
		return build(config);
	}

	public <T> CaffeineLocalCache<T> build(CaffeineLocalCacheConfig config) {
		Cache<String,Value<T>> caffeine = Caffeine.newBuilder()
							.maximumSize(config.getMaximumSize())
							.expireAfterWrite(config.getExpireAfterWrite())
							.refreshAfterWrite(config.getRefreshAfterWrite())
							.build();
		return new CaffeineLocalCache<>(config, caffeine);
	}

}
