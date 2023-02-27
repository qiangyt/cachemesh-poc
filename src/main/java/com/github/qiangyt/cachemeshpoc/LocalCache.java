package com.github.qiangyt.cachemeshpoc;

public interface LocalCache<V> extends AutoCloseable {

	public static interface Builder {
		<V> LocalCache<V> build(String name, Class<V> valueClass);
	}

	String name();

	Class<V> valueClass();

	CacheEntry<V> getSingle(String key);

	void setSingle(CacheEntry<V> entry);

}
