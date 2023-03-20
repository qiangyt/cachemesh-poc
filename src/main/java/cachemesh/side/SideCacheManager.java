package cachemesh.side;

import java.util.concurrent.ConcurrentHashMap;

import cachemesh.common.Hashing;
import cachemesh.spi.LocalCacheManager;
import cachemesh.spi.NodeCacheManager;

public class SideCacheManager implements NodeCacheManager {

	private final ConcurrentHashMap<String, SideCache> caches = new ConcurrentHashMap<>();

	@lombok.Getter
	private final LocalCacheManager<byte[]> localCacheManager;

	@lombok.Getter
	private final Hashing hashing;

	public SideCacheManager(LocalCacheManager<byte[]> localCacheManager, Hashing hashing) {
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
			var c = this.localCacheManager.resolve(cacheName, byte[].class);
			return new SideCache(c, hashing);
		});
	}

	@Override
	public void close() throws Exception {
	}

}
