package cachemesh;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.err.ServiceException;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import cachemesh.spi.NodeCache;
import cachemesh.spi.base.Value;
import static net.logstash.logback.argument.StructuredArguments.kv;

public class MeshCache<T> implements HasName {

	private Logger logger;

	private final LocalCache<T, Value<T>, LocalCacheConfig<T>> nearCache;

	private final MeshNetwork network;

	public MeshCache(LocalCache<T, Value<T>, LocalCacheConfig<T>> nearCache, MeshNetwork network) {
		this.nearCache = nearCache;
		this.network = network;

		this.logger = LogHelper.getLogger(this);
	}

	@Override
	public String getName() {
		return this.nearCache.getName();
	}

	public NodeCache resolveNodeCache(String key) {
		var node = this.network.findNode(key);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("find node for {}: {}", kv("key", key), LogHelper.kv("node", node));
		}

		return node.getNodeCache();
	}

	public T getSingle(String key) {
		var near = this.nearCache;

		var nearValue = near.getSingle(key);
		long version = (nearValue == null) ? 0 : nearValue.getVersion();

		var nodeCache = resolveNodeCache(key);
		var r = nodeCache.getSingle(getName(), key, version);

		switch(r.getStatus()) {
			case OK: {
				var cfg = near.getConfig();
				T obj = cfg.getSerder().deserialize(r.getValue(), cfg.getValueClass());
				near.putSingle(key, new Value<>(obj, r.getVersion()));
				return obj;
			}
			case NO_CHANGE:	return (T)nearValue.getData();
			case NOT_FOUND: {
				this.nearCache.invalidateSingle(key);
				return null;
			}
			case REDIRECT: {
				this.nearCache.invalidateSingle(key);
				throw new InternalException("TODO");
			}
			default: throw new ServiceException("unexpected status");
		}
	}

	public void putSingle(String key, T object) {
		var nodeCache = resolveNodeCache(key);

		var cfg = this.nearCache.getConfig();
		var bytes = cfg.getSerder().serialize(object);
		long version = nodeCache.putSingle(getName(), key, bytes);
		this.nearCache.putSingle(key, new Value<>(object, version));
	}

}
