package cachemeshpoc.caffeine;

import java.util.Collection;
import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemeshpoc.local.LocalCache;

public class CaffeineCache<T> implements LocalCache<T> {

	@lombok.Getter
	private final String name;

	@lombok.Getter
	private final Class<T> valueClass;

	@lombok.Getter
	private final CaffeineCacheConfig config;

	private final Cache<String, T> caffeine;

	public CaffeineCache(String name, Class<T> valueClass, CaffeineCacheConfig cfg) {
		this.name = name;
		this.valueClass = valueClass;
		this.config = cfg;
		this.caffeine = createCaffeineInstance(cfg);
	}

	protected Cache<String, T> createCaffeineInstance(CaffeineCacheConfig cfg) {
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
