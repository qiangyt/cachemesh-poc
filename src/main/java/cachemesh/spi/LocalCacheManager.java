package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.Mappable;
import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.base.Value;

import org.slf4j.Logger;

@ThreadSafe
public class LocalCacheManager<T, V extends Value<T>, K extends LocalCache<T, V, C>, C extends LocalCacheConfig<T>>
	implements Mappable {

	class Item {
		K cache;
		C config;
	}

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, Item> items = new ConcurrentHashMap<>();

	@lombok.Getter
	private final C defaultConfig;

	private ShutdownSupport shutdown;


	public LocalCacheManager(ShutdownSupport shutdown, C defaultConfig) {
		this.shutdown = shutdown;
		this.defaultConfig = defaultConfig;
	}


	public void addConfig(C config) {
		this.items.compute(config.getName(), (name, item) -> {
			if (item != null) {
				throw new InternalException("duplicated configuration %s", name);
			}

			item = new Item();
			item.config = config;
			return item;
		});
	}


	public C getConfig(String name) {
		var i = this.items.get(name);
		return (i == null) ? null : i.config;
	}


	public K get(String name) {
		var i = this.items.get(name);
		return (i == null) ? null : i.cache;
	}

	@SuppressWarnings("unchecked")
	public K resolve(String name, Class<T> valueClass) {
		var i = this.items.compute(name, (n, item) -> {
			if (item == null) {
				item = new Item();
				item.config = this.defaultConfig.buildAnother(name, valueClass);
			}

			if (item.cache == null) {
				var r = (K)item.config.getFactory().create(item.config);
				if (this.shutdown != null) {
					this.shutdown.register(r);
				}
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("cache not found, so create it: {}", LogHelper.kv("config", item.config));
				}
			}

			return item;
		});
		return i.cache;
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		//r.put("caches", HasName.toMaps(this.caches));
		r.put("defaultConfig", this.defaultConfig.toMap());

		return r;
	}

}
