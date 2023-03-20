package cachemesh.lettuce;

import java.util.concurrent.ConcurrentHashMap;

import cachemesh.spi.NodeCache;
import cachemesh.spi.NodeCacheManager;


public class LettuceCacheManager implements NodeCacheManager {

	private final ConcurrentHashMap<String, LettuceCache> caches = new ConcurrentHashMap<>();

	private final LettuceClient client;

	public LettuceCacheManager(LettuceClient client) {
		this.client = client;
	}

	@Override
	public NodeCache get(String cacheName) {
		return this.caches.get(cacheName);
	}

	@Override
	public NodeCache resolve(String cacheName) {
		return this.caches.computeIfAbsent(cacheName, k -> new LettuceCache(cacheName, this.client));
	}

	@Override
	public void close() throws Exception {
	}

}
