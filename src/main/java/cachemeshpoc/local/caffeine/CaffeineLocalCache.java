package cachemeshpoc.local.caffeine;

import java.util.Collection;
import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;

import cachemeshpoc.local.Entry;
import cachemeshpoc.local.Entry.Value;
import cachemeshpoc.local.base.BaseLocalCache;

public class CaffeineLocalCache<T> extends BaseLocalCache<T> {

	private final Cache<String, Value<T>> caffeine;

	public CaffeineLocalCache(CaffeineLocalCacheConfig config, Cache<String, Value<T>> caffeine) {
		super(config);
		this.caffeine = caffeine;
	}

	@Override
	public void invalidateSingle(String key) {
		this.caffeine.invalidate(key);
	}

	@Override
	public void invalidateMultiple(Collection<String> keys) {
		this.caffeine.invalidateAll(keys);
	}

	@Override
	public Value<T> getSingle(String key) {
		return this.caffeine.getIfPresent(key);
	}

	@Override
	public void putSingle(String key, Value<T> value) {
		this.caffeine.put(key, value);
	}

	@Override
	public Map<String, Value<T>> getMultiple(Collection<String> keys) {
		return this.caffeine.getAllPresent(keys);
	}

	@Override
	public void putMultiple(Collection<Entry<T>> entries) {
		this.caffeine.putAll(Entry.toMap(entries));
	}

	@Override
	public void close() throws Exception {
		this.caffeine.cleanUp();
	}

	@Override
	public Collection<String> getAllKeys() {
		return this.caffeine.asMap().keySet();
	}

}
