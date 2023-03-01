package cachemeshpoc.local.caffeine;

import java.util.Collection;
import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;

import cachemeshpoc.local.Entry;
import cachemeshpoc.local.EntryValue;
import cachemeshpoc.local.base.BaseLocalCache;

public class CaffeineLocalCache extends BaseLocalCache {

	private final Cache<String, EntryValue> caffeine;

	public CaffeineLocalCache(CaffeineLocalCacheConfig config, Cache<String, EntryValue> caffeine) {
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
	public EntryValue getSingle(String key) {
		return this.caffeine.getIfPresent(key);
	}

	@Override
	public void putSingle(String key, EntryValue value) {
		this.caffeine.put(key, value);
	}

	@Override
	public Map<String, EntryValue> getMultiple(Collection<String> keys) {
		return this.caffeine.getAllPresent(keys);
	}

	@Override
	public void putMultiple(Collection<Entry> entries) {
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
