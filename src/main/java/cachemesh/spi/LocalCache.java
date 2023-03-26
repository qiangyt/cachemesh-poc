package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.spi.base.Value;

public interface LocalCache
	<T, V extends Value<T>, C extends LocalCacheConfig<T>>
	extends Shutdownable {

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

	V getSingle(String key, long version);

	//Map<String, Value<T>> getMultiple(Collection<String> keys);

	V putSingle(String key, BiFunction<String, V, V> mapper);

	//void putMultiple(Collection<LocalCacheEntry<T>> entries);

	//Collection<String> getAllKeys();

}
