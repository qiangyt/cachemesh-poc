package cachemesh.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import cachemesh.common.HasName;
import cachemesh.spi.base.LocalCacheEntry;
import cachemesh.spi.base.Value;

public interface CommonCache<T, C extends CommonCacheConfig<T>> extends AutoCloseable, HasName {

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

	C getConfig();

	//void invalidateSingle(String key);

	//void invalidateMultiple(Collection<String> keys);

	Value<T> getSingle(String key, long version);

	//Map<String, Value<T>> getMultiple(Collection<String> keys);

	Value<T> putSingle(String key, BiFunction<String, Value<T>, Value<T>> mapper);

	//void putMultiple(Collection<LocalCacheEntry<T>> entries);

	//Collection<String> getAllKeys();

}
