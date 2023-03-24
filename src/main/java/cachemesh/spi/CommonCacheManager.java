package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.Closer;
import cachemesh.common.Mappable;
import cachemesh.common.err.InternalException;
import cachemesh.common.HasName;
import cachemesh.common.util.LogHelper;

import org.slf4j.Logger;

@ThreadSafe
public class CommonCacheManager<T, K extends CommonCache<T, C>, C extends CommonCacheConfig<T>>
	implements AutoCloseable, Mappable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, K> caches = new ConcurrentHashMap<>();

	private final Map<String, C> configs = new ConcurrentHashMap<>();

	@lombok.Getter
	private final C defaultConfig;

	public CommonCacheManager(C defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	public void addConfig(C config) {
		String name = config.getName();
		if (this.configs.containsKey(name)) {
			throw new InternalException("duplicated configuration %s", name);
		}
		this.configs.put(name, config);
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


	public K get(String name) {
		return this.caches.get(name);
	}

	@SuppressWarnings("unchecked")
	public K resolve(String name, Class<T> valueClass) {
		return this.caches.computeIfAbsent(name, k -> {
			var c = resolveConfig(name, valueClass);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("cache not found, so build it: {}", LogHelper.kv("config", c));
			}
			return (K)c.getFactory().create(c);
		});
	}

	@SuppressWarnings("unchecked")
	public C resolveConfig(String name, Class<T> valueClass) {
		return this.configs.computeIfAbsent(name, n -> {
			var c = this.defaultConfig.buildAnother(name, valueClass);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("cache configuration not found, so try default one: {}", LogHelper.kv("config", c));
			}
			return (C)c;
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
