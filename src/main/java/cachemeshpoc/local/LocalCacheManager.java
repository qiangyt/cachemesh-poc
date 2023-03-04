package cachemeshpoc.local;

import java.util.concurrent.ConcurrentHashMap;

import cachemeshpoc.err.MeshInternalException;
import cachemeshpoc.local.LocalCache.Entry;
import cachemeshpoc.local.LocalCache.Factory;

public class LocalCacheManager implements AutoCloseable {

	private final ConcurrentHashMap<String, LocalCache<?>> caches = new ConcurrentHashMap<>();

	@lombok.Getter
	private final Factory factory;

	public LocalCacheManager(Factory factory) {
		this.factory = factory;
	}

	@SuppressWarnings("all")
	public void register(LocalCache newCache, boolean mergeWithOld) {
		String name = newCache.getName();

		var old = this.caches.get(name);
		if (old != null) {
			var oldType = old.getValueClass();
			var newType = newCache.getValueClass();
			if (oldType.equals(newType) == false) {
				throw new MeshInternalException("cannot merge 2 caches with different value types: %s <--> %s",
						oldType, newType);
			}

			if (mergeWithOld) {
				var oldEntries = Entry.fromMap(old.getMultiple(old.getAllKeys()));
				newCache.putMultiple(oldEntries);
			}
		}

		this.caches.put(name, newCache);
	}

	@SuppressWarnings("unchecked")
	public <T> LocalCache<T> get(String cacheName) {
		return (LocalCache<T>)this.caches.get(cacheName);
	}

	@SuppressWarnings("unchecked")
	public <T> LocalCache<T> resolve(String cacheName, Class<T> valueClass) {
		var r = this.caches.computeIfAbsent(cacheName, k -> this.factory.create(cacheName, valueClass));
		return (LocalCache<T>)r;
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
