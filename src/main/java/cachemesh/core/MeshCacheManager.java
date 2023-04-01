package cachemesh.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class MeshCacheManager {

	private final Map<String, MeshCache<?>> caches = new ConcurrentHashMap<>();

	private final LocalCacheManager nearCacheManager;

	private final MeshNetwork network;

	public MeshCacheManager(LocalCacheManager nearCacheManager, MeshNetwork network) {
		this.nearCacheManager = nearCacheManager;
		this.network = network;
	}

	@SuppressWarnings("unchecked")
	public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
		return (MeshCache<T>) this.caches.computeIfAbsent(cacheName, k -> {
			return new MeshCache<>(cacheName, getNearCacheManager(), getNetwork());
		});
	}

}
