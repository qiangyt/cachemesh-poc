package com.github.qiangyt.cachemeshpoc.local;

public interface LocalCacheManager {

	<V> LocalCache<V> get(String name, Class<V> valueClass);

	void registerAlways(String name, LocalCacheBuilder builder);

	void registerIfAbsent(String name, LocalCacheBuilder builder);

	<V> LocalCache<V> create(String name, LocalCacheBuilder builder);

	<V> LocalCache<V> resolve(String name, LocalCacheBuilder builder);

}
