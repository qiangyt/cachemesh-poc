package cachemeshpoc.grpc;

import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.NodeCache;

public class GrpcCacheManager implements NodeCache.Manager {

	private final ConcurrentHashMap<String, GrpcCache> caches = new ConcurrentHashMap<>();

	private final GrpcClient client;

	public GrpcCacheManager(GrpcClient client) {
		this.client = client;
	}

	@Override
	public NodeCache get(String cacheName) {
		return this.caches.get(cacheName);
	}

	@Override
	public NodeCache resolve(String cacheName) {
		return this.caches.computeIfAbsent(cacheName, k -> new GrpcCache(cacheName, this.client));
	}

	@Override
	public void close() throws Exception {
	}

}
