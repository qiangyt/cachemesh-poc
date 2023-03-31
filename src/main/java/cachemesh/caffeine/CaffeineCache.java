package cachemesh.caffeine;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.spi.LocalCache;
import cachemesh.spi.Value;

@Getter
public class CaffeineCache
	extends AbstractShutdownable
	implements LocalCache {

	private final CaffeineConfig config;

	private final Cache<String, Value> instance;


	public CaffeineCache(CaffeineConfig config, Cache<String, Value> instance, ShutdownSupport shutdownSupport) {
		super(config.getName(), shutdownSupport);

		this.config = config;
		this.instance = instance;
	}

	@Override
	public String toString() {
		return getConfig().toString();
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		this.instance.cleanUp();
	}


	@Override
	public void invalidateSingle(String key) {
		this.instance.invalidate(key);
	}

	@Override
	public void invalidateMultiple(Collection<String> keys) {
		this.instance.invalidateAll(keys);
	}

	@Override
	public Value getSingle(String key) {
		return this.instance.getIfPresent(key);
	}

	@Override
	public Value putSingle(String key, BiFunction<String, Value, Value> mapper) {
		return this.instance.asMap().compute(key, mapper);
	}

	@Override
	public Map<String, Value> getMultiple(Collection<String> keys) {
	 	return this.instance.getAllPresent(keys);
	}

	// @Override
	// public void putMultiple(Collection<LocalCacheEntry<T>> entries) {
	// 	this.instance.putAll(LocalCacheEntry.toMap(entries));
	// }

	@Override
	public Collection<String> getAllKeys() {
		return this.instance.asMap().keySet();
	}


}
