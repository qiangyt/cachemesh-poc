package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import cachemesh.common.Closer;
import cachemesh.common.Mappable;
import cachemesh.common.HasName;
import cachemesh.common.util.LogHelper;

import org.slf4j.Logger;

public class LocalCacheManager<T> implements AutoCloseable, Mappable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, LocalCache<T>> caches = new ConcurrentHashMap<>();

	private final Map<String, LocalCacheConfig<T>> configs = new ConcurrentHashMap<>();

	@lombok.Getter
	private final LocalCacheConfig<T> defaultConfig;

	public LocalCacheManager(LocalCacheConfig<T> defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	/*@SuppressWarnings("all")
	public void add(LocalCache newCache, boolean mergeWithOld) {
		String name = newCache.getName();

		var old = this.caches.get(name);
		if (old != null) {
			var oldType = old.getValueClass();
			var newType = newCache.getValueClass();
			if (oldType.equals(newType) == false) {
				throw new MeshInternalException("cannot merge 2 caches with different value types: %s <--> %s",
						oldType, newType);
			}

			if (mergeWithOld) {
				var oldEntries = LocalCacheEntry.fromMap(old.getMultiple(old.getAllKeys()));
				newCache.putMultiple(oldEntries);
			}
		}

		this.caches.put(name, newCache);
	}*/

	public LocalCache<T> resolve(String name, Class<T> valueClass) {
		var r = this.caches.computeIfAbsent(name, k -> {
			var c = resolveConfig(name, valueClass);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("cache not found, so build it: {}", LogHelper.kv("config", c));
			}
			return c.getBuilder().build(c);
		});
		return (LocalCache<T>)r;
	}

	public LocalCacheConfig<T> resolveConfig(String name, Class<T> valueClass) {
		return this.configs.computeIfAbsent(name, k -> {
			var c = this.defaultConfig.buildAnother(name, valueClass);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("cache configuration not found, so build default one: {}", LogHelper.kv("config", c));
			}
			return c;
		});
	}

	@Override
	public void close() throws Exception {
		Closer.closeSilently(this.caches.values(), this.logger);
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("caches", HasName.toMaps(this.caches));
		r.put("configs", HasName.toMaps(this.configs));
		r.put("defaultConfig", this.defaultConfig.toMap());

		return r;
	}

}
