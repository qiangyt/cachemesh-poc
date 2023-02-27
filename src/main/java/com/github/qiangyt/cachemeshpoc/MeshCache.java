package com.github.qiangyt.cachemeshpoc;

public interface MeshCache<V> extends AutoCloseable {

	public static interface Manager {
		<V> MeshCache<V> get(String name, Class<V> valueClass);

		void registerAlways(String name, LocalCache.Builder localBuilder, RemoteCache.Builder remoteBuilder);

		void registerIfAbsent(String name, LocalCache.Builder localBuilder, RemoteCache.Builder remoteBuilder);

		<V> MeshCache<V> create(String name, LocalCache.Builder localBuilder, RemoteCache.Builder remoteBuilder);

		<V> MeshCache<V> resolve(String name, LocalCache.Builder localBuilder,
		RemoteCache.Builder remoteBuilder);
	}

	String name();

	Class<V> valueClass();

	V getSingle(String key);

	void setSingle(String key, V value);

}
