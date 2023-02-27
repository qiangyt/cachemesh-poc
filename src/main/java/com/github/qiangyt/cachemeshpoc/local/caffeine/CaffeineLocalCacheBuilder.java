package com.github.qiangyt.cachemeshpoc.local.caffeine;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.qiangyt.cachemeshpoc.local.LocalCache;
import com.github.qiangyt.cachemeshpoc.local.LocalCacheBuilder;
import com.github.qiangyt.cachemeshpoc.local.LocalEntry;

public class CaffeineLocalCacheBuilder implements LocalCacheBuilder {

	@Override
	public <V> LocalCache<V> build(String name, Class<V> valueClass) {
		Cache<String,LocalEntry<V>> caffeine = Caffeine.newBuilder()
				.maximumSize(10_000)
				.expireAfterWrite(Duration.ofMinutes(5))
				.refreshAfterWrite(Duration.ofMinutes(1))
				.build();
		return new CaffeineLocalCache<V>(name, valueClass, caffeine);
	}

}
