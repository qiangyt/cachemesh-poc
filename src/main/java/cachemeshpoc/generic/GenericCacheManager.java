package cachemeshpoc.generic;

import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.err.CacheMeshInternalException;
import cachemeshpoc.generic.GenericCache.Factory;

public class GenericCacheManager implements AutoCloseable {

	private final ConcurrentHashMap<String, GenericCache<?>> caches = new ConcurrentHashMap<>();

	private final Factory factory;

	public GenericCacheManager(Factory factory) {
		this.factory = factory;
	}

	@SuppressWarnings("all")
	public void register(GenericCache newCache, boolean mergeWithOld) {
		String name = newCache.getName();

		var old = this.caches.get(name);
		if (old != null) {
			var oldType = old.getValueClass();
			var newType = newCache.getValueClass();
			if (oldType.equals(newType) == false) {
				throw new CacheMeshInternalException("cannot merge 2 caches with different value types: %s <--> %s",
						oldType, newType);
			}

			if (mergeWithOld) {
				var oldEntries = GenericEntry.fromMap(old.getMultipleAnyhow(old.getAllKeys()));
				newCache.putMultiple(oldEntries);
			}
		}

		this.caches.put(name, newCache);
	}

	@SuppressWarnings("unchecked")
	public <T> GenericCache<T> get(String cacheName) {
		return (GenericCache<T>)this.caches.get(cacheName);
	}

	@SuppressWarnings("unchecked")
	public <T> GenericCache<T> resolve(String cacheName, Class<T> valueClass) {
		var r = this.caches.computeIfAbsent(cacheName, k -> this.factory.create(cacheName, valueClass));
		return (GenericCache<T>)r;
	}

	@Override
	public void close() throws Exception {
		for (var cache : this.caches.values()) {
			try {
				cache.close();
			} catch (Exception e) {
				// TODO
			}
		}
	}

}
