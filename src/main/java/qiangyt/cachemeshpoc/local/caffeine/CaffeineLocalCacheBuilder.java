package qiangyt.cachemeshpoc.local.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import qiangyt.cachemeshpoc.local.LocalCache;
import qiangyt.cachemeshpoc.local.LocalCacheBuilder;
import qiangyt.cachemeshpoc.local.EntryValue;

public class CaffeineLocalCacheBuilder implements LocalCacheBuilder {

	@Override
	public LocalCache build(String cacheName) {
		var config = CaffeineLocalCacheConfig.buildDefault(cacheName);
		return build(config);
	}

	public CaffeineLocalCache build(CaffeineLocalCacheConfig config) {
		Cache<String,EntryValue> caffeine = Caffeine.newBuilder()
							.maximumSize(config.getMaximumSize())
							.expireAfterWrite(config.getExpireAfterWrite())
							.refreshAfterWrite(config.getRefreshAfterWrite())
							.build();
		return new CaffeineLocalCache(config, caffeine);
	}

}
