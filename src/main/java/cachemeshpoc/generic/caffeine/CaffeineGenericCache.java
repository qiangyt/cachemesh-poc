package cachemeshpoc.generic.caffeine;

import java.util.Collection;
import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemeshpoc.generic.BaseGenericCache;
import cachemeshpoc.generic.GenericEntry;
import cachemeshpoc.generic.GenericEntry.Value;

public class CaffeineGenericCache<T> extends BaseGenericCache<T> {

	@lombok.Getter
	private final CaffeineGenericCacheConfig config;

	private final Cache<String, Value<T>> caffeine;

	public CaffeineGenericCache(String name, Class<T> valueClass, CaffeineGenericCacheConfig cfg) {
		super(name, valueClass);

		this.config = cfg;
		this.caffeine = createCaffeineInstance(cfg);
	}

	protected Cache<String, Value<T>> createCaffeineInstance(CaffeineGenericCacheConfig cfg) {
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

	public Value<T> getSingleAnyhow(String key) {
		return this.caffeine.getIfPresent(key);
	}

	@Override
	public void putSingle(String key, Value<T> value) {
		this.caffeine.put(key, value);
	}

	@Override
	public Map<String, Value<T>> getMultipleAnyhow(Collection<String> keys) {
		return this.caffeine.getAllPresent(keys);
	}

	@Override
	public void putMultiple(Collection<GenericEntry<T>> entries) {
		this.caffeine.putAll(GenericEntry.toMap(entries));
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
