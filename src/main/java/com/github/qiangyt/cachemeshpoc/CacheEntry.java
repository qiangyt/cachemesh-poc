package com.github.qiangyt.cachemeshpoc;

public interface CacheEntry<V> {

	String key();

	V value();

	long version();

}
