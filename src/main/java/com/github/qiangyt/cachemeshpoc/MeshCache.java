package com.github.qiangyt.cachemeshpoc;

public interface MeshCache<V> extends AutoCloseable {

	String getName();

	Class<V> getValueClass();

	V getSingle(String key);

	void setSingle(String key, V value);

}
