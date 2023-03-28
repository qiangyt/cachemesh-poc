package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.spi.base.Value;

public interface ByteCache
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

	ByteCacheConfig getConfig();

	// void invalidateSingle(String key);

	// void invalidateMultiple(Collection<String> keys);

	Value<byte[]> getSingle(String key, long version);

	// Map<String, Value<byte[]>> getMultiple(Collection<String> keys);

	Value<byte[]> putSingle(String key, BiFunction<String, Value<byte[]>, Value<byte[]>> mapper);

	// void putMultiple(Collection<LocalCacheEntry<byte[]>> entries);

	// Collection<String> getAllKeys();

}
