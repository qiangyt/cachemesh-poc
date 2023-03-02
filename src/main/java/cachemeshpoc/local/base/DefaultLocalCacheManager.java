package cachemeshpoc.local.base;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.err.CacheMeshInternalException;
import cachemeshpoc.local.Entry;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalCache.Builder;
import cachemeshpoc.local.LocalCache.Manager;

public class DefaultLocalCacheManager implements Manager {

	private final ConcurrentHashMap<String, LocalCache<Object>> caches = new ConcurrentHashMap<>();

	private final Builder defaultBuilder;

	public DefaultLocalCacheManager(Builder defaultBuilder) {
		this.defaultBuilder = defaultBuilder;
	}

	@Override @SuppressWarnings("unchecked")
	public void register(LocalCache<?> cache) {
		String cacheName = cache.getConfig().getName();

		var prev = this.caches.get(cacheName);
		if (prev != null) {
			var prevType = prev.getConfig().getValueClass();
			var newType = cache.getConfig().getValueClass();
			if (prevType.equals(newType) == false) {
				throw new CacheMeshInternalException("cannot merge 2 caches with different value types: %s <--> %s",
							prevType, newType);
			}

			Collection<Entry<Object>> entries = Entry.fromMap(prev.getMultiple(prev.getAllKeys()));
			((LocalCache<Object>)cache).putMultiple(entries);
		}

		this.caches.put(cacheName, ((LocalCache<Object>)cache));
	}

	@Override @SuppressWarnings("unchecked")
	public <T> LocalCache<T> get(String cacheName) {
		return (LocalCache<T>)this.caches.get(cacheName);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> LocalCache<T> resolve(String cacheName) {
		return (LocalCache<T>)this.caches.computeIfAbsent(cacheName, k -> this.defaultBuilder.build(cacheName));
	}

}
