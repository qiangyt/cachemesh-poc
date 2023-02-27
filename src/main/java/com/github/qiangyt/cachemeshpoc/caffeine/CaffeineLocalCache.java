package com.github.qiangyt.cachemeshpoc.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.qiangyt.cachemeshpoc.CacheEntry;
import com.github.qiangyt.cachemeshpoc.impl.AbstractCache;

public class CaffeineLocalCache<V> extends AbstractCache<V> {

	private final Cache<String, CacheEntry<V>> caffeine;

	public CaffeineLocalCache(String name, Class<V> valueClass, Cache<String, CacheEntry<V>> caffeine) {
		super(name, valueClass);
		this.caffeine = caffeine;
	}

	@Override
	public CacheEntry<V> getSingle(String key) {
		return this.caffeine.getIfPresent(key);
	}

	@Override
	public void setSingle(CacheEntry<V> entry) {
		this.caffeine.put(entry.key(), entry);
	}

	@Override
	public void close() throws Exception {
		this.caffeine.cleanUp();
	}

}
