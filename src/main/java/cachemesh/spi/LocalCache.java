package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.spi.base.Value;

public interface LocalCache	extends Shutdownable {

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

	LocalCacheConfig getConfig();

	//void invalidateSingle(String key);

	//void invalidateMultiple(Collection<String> keys);

	Value getSingle(String key);

	//Map<String, Value<T>> getMultiple(Collection<String> keys);

	Value putSingle(String key, BiFunction<String, Value, Value> mapper);

	//void putMultiple(Collection<LocalCacheEntry<T>> entries);

	//Collection<String> getAllKeys();

}
