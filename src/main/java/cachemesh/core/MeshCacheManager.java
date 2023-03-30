package cachemesh.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import cachemesh.MeshCache;
import cachemesh.core.local.LocalCacheManager;
import lombok.Getter;

public class MeshCacheManager {

	private final Map<String, MeshCache<?>> caches = new ConcurrentHashMap<>();

	@Getter
	private final LocalCacheManager nearCacheManager;

	@Getter
	private final MeshNodeManager nodeManager;

	public MeshCacheManager(LocalCacheManager nearCacheManager, MeshNodeManager nodeManager) {
		this.nearCacheManager = nearCacheManager;
		this.nodeManager = nodeManager;
	}

	@SuppressWarnings("unchecked")
	public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
		return (MeshCache<T>) this.caches.computeIfAbsent(cacheName, k -> {
			return new MeshCache<>(cacheName, getNearCacheManager(), getNodeManager());
		});
	}

}
