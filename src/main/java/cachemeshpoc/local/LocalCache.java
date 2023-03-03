package cachemeshpoc.local;

import java.util.Collection;
import java.util.Map;

import cachemeshpoc.CacheEntry;

public interface LocalCache<T> extends AutoCloseable {

	public static interface Factory {
		<T> LocalCache<T> create(String cacheName, Class<T> valueClass);
	}

	String getName();

	Class<T> getValueClass();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	T getSingle(String key);

	Map<String, T> getMultiple(Collection<String> keys);

	void putSingle(String key, T value);

	void putMultiple(Collection<CacheEntry<T>> entries);

	Collection<String> getAllKeys();

}
