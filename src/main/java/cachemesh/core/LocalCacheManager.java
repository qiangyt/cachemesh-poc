package cachemesh.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;

import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.common.shutdown.Shutdownable;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
@ThreadSafe
public class LocalCacheManager implements Shutdownable {

	class Item {
		LocalCache cache;
		LocalCacheConfig config;
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, Item> items = new ConcurrentHashMap<>();

	private final LocalCacheConfig defaultConfig;

	private final ShutdownManager shutdownManager;

	private final String name;

	public LocalCacheManager(String name, LocalCacheConfig defaultConfig, ShutdownManager shutdownManager) {
		this.name = name;
		this.defaultConfig = defaultConfig;

		this.shutdownManager = shutdownManager;
		if (shutdownManager != null) {
			shutdownManager.register(this);
		}
	}

	public void addConfig(LocalCacheConfig config) {
		this.items.compute(config.getName(), (name, item) -> {
			if (item != null) {
				throw new InternalException("duplicated configuration %s", name);
			}

			item = new Item();
			item.config = config;
			return item;
		});
	}


	public LocalCacheConfig getConfig(String name) {
		var i = this.items.get(name);
		return (i == null) ? null : i.config;
	}


	public LocalCache get(String name) {
		var i = this.items.get(name);
		return (i == null) ? null : i.cache;
	}

	public LocalCache resolve(String name, Class<?> valueClass) {
		var i = this.items.compute(name, (n, item) -> {
			if (item == null) {
				item = new Item();
				item.config = this.defaultConfig.buildAnother(name, valueClass);
			}

			if (item.cache == null) {
				item.cache = item.config.getFactory().create(item.config);
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

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		if (isShutdownNeeded() == false) {
			throw new IllegalStateException(getName() + " doesn't need shutdown");
		}

		var sd = getShutdownManager();
		if (sd != null) {
			sd.shutdown(this, timeoutSeconds);
		} else {
			onShutdown(createShutdownLogger(), timeoutSeconds);
		}
	}

	public ShutdownLogger createShutdownLogger() {
		return new ShutdownLogger(getLogger());
	}


	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		var copy = new ArrayList<Item>(this.items.values());
		for (var item: copy) {
			var c = item.cache;
			if (c != null) {
				c.shutdown(timeoutSeconds);
			}
		}
	}

}
