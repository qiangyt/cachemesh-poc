package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.Mappable;
import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.HasName;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.base.Value;

import org.slf4j.Logger;

@ThreadSafe
public class LocalCacheManager<T, V extends Value<T>, K extends LocalCache<T, V, C>, C extends LocalCacheConfig<T>>
	implements Mappable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, K> caches = new ConcurrentHashMap<>();

	private final Map<String, C> configs = new ConcurrentHashMap<>();

	@lombok.Getter
	private final C defaultConfig;

	private ShutdownSupport shutdown;

	public LocalCacheManager(ShutdownSupport shutdown, C defaultConfig) {
		this.shutdown = shutdown;
		this.defaultConfig = defaultConfig;
	}

	public void addConfig(C config) {
		String name = config.getName();
		if (this.configs.containsKey(name)) {
			throw new InternalException("duplicated configuration %s", name);
		}
		this.configs.put(name, config);
	}


	public K get(String name) {
		return this.caches.get(name);
	}

	@SuppressWarnings("unchecked")
	public K resolve(String name, Class<T> valueClass) {
		return this.caches.computeIfAbsent(name, k -> {
			var c = resolveConfig(name, valueClass);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("cache not found, so create it: {}", LogHelper.kv("config", c));
			}

			var r = (K)c.getFactory().create(c);
			if (this.shutdown != null) {
				this.shutdown.register(r);
			}

			return r;
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
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("caches", HasName.toMaps(this.caches));
		r.put("configs", HasName.toMaps(this.configs));
		r.put("defaultConfig", this.defaultConfig.toMap());

		return r;
	}

}
