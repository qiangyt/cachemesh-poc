package com.github.qiangyt.cachemeshpoc.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.qiangyt.cachemeshpoc.MeshCache;
import com.github.qiangyt.cachemeshpoc.MeshCacheBuilder;
import com.github.qiangyt.cachemeshpoc.MeshCacheManager;

public class DefaultMeshCacheManager implements MeshCacheManager {

	public static final DefaultMeshCacheManager DEFAULT = new DefaultMeshCacheManager();

	static class Holder<V> {
		MeshCache<V> cache;
		final MeshCacheBuilder<V> builder;

		Holder(MeshCacheBuilder<V> builder) {
			this.builder = builder;
		}

		void build() {
			this.cache = this.builder.build();
		}

		MeshCache<V> get() {
			return this.cache;
		}
	}

	private final Map<String, Holder<?>> caches = new ConcurrentHashMap<>();

	@Override
	public <V> MeshCache<V> get(String name, Class<V> valueClass) {
		@SuppressWarnings("unchecked")
		var h = (Holder<V>)this.caches.get(name);

		return h.get();
	}

	@Override
	public void registerAlways(String name, MeshCacheBuilder<?> builder) {
		doRegisterAlways(name, builder);
	}

	<V> Holder<V> doRegisterAlways(String name, MeshCacheBuilder<V> builder) {
		var h = new Holder<>(builder);

		var r = this.caches.computeIfAbsent(name, k -> {
			h.build();
			return h;
		});
		if (h != r) {
			throw new IllegalStateException("cache already exists: " + name);
		}

		return h;
	}

	@Override
	public void registerIfAbsent(String name, MeshCacheBuilder<?> builder) {
		doRegisterIfAbsent(name, builder);
	}

	<V> Holder<V> doRegisterIfAbsent(String name, MeshCacheBuilder<V> builder) {
		@SuppressWarnings("unchecked")
		Holder<V> r = (Holder<V>)this.caches.computeIfAbsent(name, k -> {
			var h = new Holder<>(builder);
			h.build();
			return h;
		});

		return r;
	}

	@Override
	public <V> MeshCache<V> create(String name, MeshCacheBuilder<V> builder) {
		var h = doRegisterAlways(name, builder);
		return h.get();
	}

	@Override
	public <V> MeshCache<V> resolve(String name, MeshCacheBuilder<V> builder) {
		var r = doRegisterIfAbsent(name, builder);
		return r.get();
	}

}
