package cachemesh.caffeine;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;

import cachemesh.spi.base.BaseCommonCache;
import cachemesh.spi.base.LocalCacheEntry;
import cachemesh.spi.base.Value;

public class CaffeineCache<T> extends BaseCommonCache<T, CaffeineCacheConfig<T>, Cache<String, Value<T>>> {

	public CaffeineCache(CaffeineCacheConfig<T> cfg, Cache<String, Value<T>> instance) {
		super(cfg, instance);
	}

	// @Override
	// public void invalidateSingle(String key) {
	// 	this.instance.invalidate(key);
	// }

	// @Override
	// public void invalidateMultiple(Collection<String> keys) {
	// 	this.instance.invalidateAll(keys);
	// }

	@Override
	public Value<T> getSingle(String key, long version) {
		return this.instance.getIfPresent(key);
	}

	@Override
	public Value<T> putSingle(String key, BiFunction<String, Value<T>, Value<T>> mapper) {
		return this.instance.asMap().compute(key, mapper);
	}

	// @Override
	// public Map<String, Value<T>> getMultiple(Collection<String> keys) {
	// 	return this.instance.getAllPresent(keys);
	// }

	// @Override
	// public void putMultiple(Collection<LocalCacheEntry<T>> entries) {
	// 	this.instance.putAll(LocalCacheEntry.toMap(entries));
	// }

	// @Override
	// public Collection<String> getAllKeys() {
	// 	return this.instance.asMap().keySet();
	// }

	@Override
	public void close() throws Exception {
		this.instance.cleanUp();
	}

}
