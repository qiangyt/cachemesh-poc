package cachemeshpoc.local.base;

import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.local.Entry;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCacheBuilder;
import cachemeshpoc.local.LocalCacheManager;

public class DefaultLocalCacheManager implements LocalCacheManager {

	private final ConcurrentHashMap<String, LocalCache> caches = new ConcurrentHashMap<>();

	private final LocalCacheBuilder defaultBuilder;

	public DefaultLocalCacheManager(LocalCacheBuilder defaultBuilder) {
		this.defaultBuilder = defaultBuilder;
	}

	@Override
	public void register(LocalCache cache) {
		String cacheName = cache.getConfig().getName();

		var prev = this.caches.get(cacheName);
		if (prev != null) {
			var entries = Entry.fromMap(prev.getMultiple(prev.getAllKeys()));
			cache.putMultiple(entries);
		}

		this.caches.put(cacheName, cache);
	}

	@Override
	public LocalCache get(String cacheName) {
		return this.caches.get(cacheName);
	}

	@Override
	public LocalCache resolve(String cacheName) {
		return this.caches.computeIfAbsent(cacheName, k -> this.defaultBuilder.build(cacheName));
	}

}