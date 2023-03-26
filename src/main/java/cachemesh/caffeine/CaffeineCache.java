package cachemesh.caffeine;

import java.util.function.BiFunction;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.spi.LocalCache;
import cachemesh.spi.base.Value;

@Getter
public class CaffeineCache<T, V extends Value<T>>
	extends AbstractShutdownable
	implements LocalCache<T, V, CaffeineConfig<T>> {

	private final CaffeineConfig<T> config;

	private final Cache<String, V> instance;


	public CaffeineCache(CaffeineConfig<T> config, Cache<String, V> instance) {
		super(config.getName());

		this.config = config;
		this.instance = instance;

		setShutdownNeeded(true);
	}

	@Override
	public String toString() {
		return getConfig().toString();
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		this.instance.cleanUp();
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
	public V getSingle(String key, long version) {
		return this.instance.getIfPresent(key);
	}

	@Override
	public V putSingle(String key, BiFunction<String, V, V> mapper) {
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


}
