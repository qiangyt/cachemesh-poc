package com.github.qiangyt.cachemeshpoc.caffeine;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.qiangyt.cachemeshpoc.CacheEntry;
import com.github.qiangyt.cachemeshpoc.LocalCache;

public class CaffeineLocalCacheBuilder implements LocalCache.Builder {

	@Override
	public <V> LocalCache<V> build(String name, Class<V> valueClass) {
		Cache<String,CacheEntry<V>> caffeine = Caffeine.newBuilder()
				.maximumSize(10_000)
				.expireAfterWrite(Duration.ofMinutes(5))
				.refreshAfterWrite(Duration.ofMinutes(1))
				.build();
		return new CaffeineLocalCache<V>(name, valueClass, caffeine);
	}

}
