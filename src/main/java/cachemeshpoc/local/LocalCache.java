package cachemeshpoc.local;

import java.util.Collection;
import java.util.Map;

public interface LocalCache<T> extends AutoCloseable {

	String getName();

	Class<T> getValueClass();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	T getSingle(String key);

	Map<String, T> getMultiple(Collection<String> keys);

	void putSingle(String key, T value);

	void putMultiple(Collection<LocalCacheEntry<T>> entries);

	Collection<String> getAllKeys();

}
