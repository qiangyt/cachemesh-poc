package com.github.qiangyt.cachemeshpoc.local.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.qiangyt.cachemeshpoc.impl.AbstractLocalCache;
import com.github.qiangyt.cachemeshpoc.local.LocalEntry;

public class CaffeineLocalCache<V> extends AbstractLocalCache<V> {

	private final Cache<String, LocalEntry<V>> caffeine;

	public CaffeineLocalCache(String name, Class<V> valueClass, Cache<String, LocalEntry<V>> caffeine) {
		super(name, valueClass);
		this.caffeine = caffeine;
	}

	@Override
	public LocalEntry<V> getSingle(String key) {
		return this.caffeine.getIfPresent(key);
	}

	@Override
	public void setSingle(LocalEntry<V> entry) {
		this.caffeine.put(entry.getKey(), entry);
	}

	@Override
	public void close() throws Exception {
		this.caffeine.cleanUp();
	}

}
