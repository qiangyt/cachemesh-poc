package cachemeshpoc;

import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCacheManager;
import cachemeshpoc.util.Hashing;
import cachemeshpoc.util.MurmurHash;

public class SideCacheManager implements NodeCache.Manager {

	private final ConcurrentHashMap<String, SideCache> caches = new ConcurrentHashMap<>();

	@lombok.Getter
	private final LocalCacheManager localCacheManager;

	@lombok.Getter
	private final Hashing hashing;

	public SideCacheManager(LocalCache.Factory localCacheFactory) {
		this(localCacheFactory, MurmurHash.DEFAULT);
	}

	public SideCacheManager(LocalCache.Factory localCacheFactory, Hashing hashing) {
		this(new LocalCacheManager(localCacheFactory), hashing);
	}

	public SideCacheManager(LocalCacheManager localCacheManager, Hashing hashing) {
		this.localCacheManager = localCacheManager;
		this.hashing = hashing;
	}

	@Override
	public SideCache get(String cacheName) {
		return this.caches.get(cacheName);
	}

	@Override
	public SideCache resolve(String cacheName) {
		return this.caches.computeIfAbsent(cacheName, k -> {
			var c = this.localCacheManager.resolve(cacheName, VershedValue.class);
			return new SideCache(c, hashing);
		});
	}

	@Override
	public void close() throws Exception {
	}

}
