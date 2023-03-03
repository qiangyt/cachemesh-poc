package cachemeshpoc.local.caffeine;

import java.util.Collection;
import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemeshpoc.CacheEntry;
import cachemeshpoc.local.BaseLocalCache;

public class CaffeineLocalCache<T> extends BaseLocalCache<T> {

	@lombok.Getter
	private final CaffeineLocalCacheConfig config;

	private final Cache<String, T> caffeine;

	public CaffeineLocalCache(String name, Class<T> valueClass, CaffeineLocalCacheConfig cfg) {
		super(name, valueClass);

		this.config = cfg;
		this.caffeine = createCaffeineInstance(cfg);
	}

	protected Cache<String, T> createCaffeineInstance(CaffeineLocalCacheConfig cfg) {
		return Caffeine.newBuilder()
							.maximumSize(cfg.getMaximumSize())
							.expireAfterWrite(cfg.getExpireAfterWrite())
							.refreshAfterWrite(cfg.getRefreshAfterWrite())
							.build();
	}

	@Override
	public String toString() {
		return super.toString() + "@caffeine";
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
	public T getSingle(String key) {
		return this.caffeine.getIfPresent(key);
	}

	@Override
	public void putSingle(String key, T value) {
		this.caffeine.put(key, value);
	}

	@Override
	public Map<String, T> getMultiple(Collection<String> keys) {
		return this.caffeine.getAllPresent(keys);
	}

	@Override
	public void putMultiple(Collection<CacheEntry<T>> entries) {
		this.caffeine.putAll(CacheEntry.toMap(entries));
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
