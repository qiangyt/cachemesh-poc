package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.Mappable;
import cachemesh.common.err.InternalException;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.base.NodeByteCache;
import cachemesh.spi.base.NodeObjectCache;

@ThreadSafe
public class NodeCacheManager implements Mappable {

	class Item {
		NodeCache cache;
		NodeCacheConfig config;
	}

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, Item> items = new ConcurrentHashMap<>();

	private final LocalCacheManager backend;

	@lombok.Getter
	private final NodeCacheConfig defaultConfig;

	public NodeCacheManager(LocalCacheManager backend, NodeCacheConfig defaultConfig) {
		this.backend = backend;
		this.defaultConfig = defaultConfig;
	}

	public void addConfig(NodeCacheConfig config) {
		this.items.compute(config.getName(), (name, item) -> {
			if (item != null) {
				throw new InternalException("duplicated configuration %s", name);
			}

			item = new Item();
			item.config = config;
			return item;
		});
	}

	public NodeCacheConfig getConfig(String name) {
		var i = this.items.get(name);
		return (i == null) ? null : i.config;
	}

	public NodeCache get(String name) {
		var i = this.items.get(name);
		return (i == null) ? null : i.cache;
	}

	public NodeCache resolve(String name) {
		var i = this.items.compute(name, (n, item) -> {
			if (item == null) {
				item = new Item();
				item.config = this.defaultConfig.buildAnother(name);
			}

			if (item.cache == null) {
				var r = create(item.config);
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

		// r.put("caches", HasName.toMaps(this.caches));
		r.put("defaultConfig", this.defaultConfig.toMap());

		return r;
	}

	protected NodeCache create(NodeCacheConfig config) {
		var name = config.getName();

		var bkCache = this.backend.get(name);
		var valueClass = bkCache.getConfig().getValueClass();
		if (valueClass == byte[].class) {
			return new NodeByteCache(config, (ByteCache) bkCache);
		}

		var objCache = new NodeObjectCache(config, bkCache);
		if (config.isCacheBytes() == false) {
			return objCache;
		}
		return new NodeObjectCache(config, bkCache);
	}

}
