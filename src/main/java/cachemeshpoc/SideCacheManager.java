package cachemeshpoc;

import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.local.LocalCacheManager;
import cachemeshpoc.util.Hashing;

public class SideCacheManager {

	private final ConcurrentHashMap<String, SideCache> caches = new ConcurrentHashMap<>();

	private final LocalCacheManager localCacheManager;

	private final Hashing hashing;

	public SideCacheManager(LocalCacheManager localCacheManager, Hashing hashing) {
		this.localCacheManager = localCacheManager;
		this.hashing = hashing;
	}

	public SideCache get(String cacheName) {
		return this.caches.get(cacheName);
	}

	public SideCache resolve(String cacheName) {
		return this.caches.computeIfAbsent(cacheName, k -> {
			var c = this.localCacheManager.resolve(cacheName, VershedValue.class);
			return new SideCache(c, hashing);
		});
	}

}
