package cachemeshpoc;

import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.generic.GenericCacheManager;

public class CombinedCacheManager implements AutoCloseable {

	private final ConcurrentHashMap<String, CombinedCache<?>> caches = new ConcurrentHashMap<>();

	private final GenericCacheManager nearCacheManager;

	private final NodeCache.Manager nodeCacheManager;

	private final Serderializer serder;

	public CombinedCacheManager(GenericCacheManager nearCacheManager, NodeCache.Manager nodeCacheManager, Serderializer serder) {
		this.nearCacheManager = nearCacheManager;
		this.nodeCacheManager = nodeCacheManager;
		this.serder = serder;
	}

	@SuppressWarnings("unchecked")
	public <T> CombinedCache<T> get(String cacheName) {
		return (CombinedCache<T>)this.caches.get(cacheName);
	}

	@SuppressWarnings("unchecked")
	public <T> CombinedCache<T> resolve(String cacheName, Class<T> valueClass) {
		return (CombinedCache<T>)this.caches.computeIfAbsent(cacheName, k -> {
			var nearCache = this.nearCacheManager.resolve(cacheName, valueClass);
			var nodeCache = this.nodeCacheManager.resolve(cacheName);
			var r = new CombinedCache<T>(nearCache, nodeCache, serder);
			return r;
		});
	}

	@Override
	public void close() throws Exception {
		try {
			this.nodeCacheManager.close();
		} finally {
			this.nearCacheManager.close();;
		}
	}

}
