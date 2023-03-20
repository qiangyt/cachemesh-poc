package cachemesh.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;
import cachemesh.spi.base.LocalCacheEntry;
import cachemesh.spi.base.Value;


public interface LocalCache<T> extends AutoCloseable, HasName {

	public static interface Factory<T> extends HasName {

		LocalCache<T> create(LocalCacheConfig<T> config);

	}


	@Override
	default Map<String, Object> toMap() {
		Map<String, Object> r = new HashMap<>();
		r.put("config", getConfig().toMap());
		return r;
	}

	@Override
	default String getName() {
		return getConfig().getName();
	}

	LocalCacheConfig<T> getConfig();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	Value<T> getSingle(String key);

	Map<String, Value<T>> getMultiple(Collection<String> keys);

	void putSingle(String key, Value<T> value);

	void putMultiple(Collection<LocalCacheEntry<T>> entries);

	Collection<String> getAllKeys();

}
