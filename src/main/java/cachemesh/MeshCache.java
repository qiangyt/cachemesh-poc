package cachemesh;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.err.ServiceException;
import cachemesh.common.util.LogHelper;
import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheManager;
import cachemesh.spi.NodeCache;
import cachemesh.spi.base.ValueImpl;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class MeshCache<T> implements HasName {

	private Logger logger;

	private final LocalCache nearCache;

	private final MeshNetwork network;

	public MeshCache(String name, LocalCacheManager localCacheManager, MeshNetwork network) {
		this.nearCache = localCacheManager.get(name);
		this.network = network;

		this.logger = LogHelper.getLogger(this);
	}

	@Override
	public String getName() {
		return this.nearCache.getName();
	}

	public NodeCache resolveNodeCache(String key) {
		var n = this.network.findNode(key);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("find node for {}: {}", kv("key", key), LogHelper.kv("node", n));
		}

		return n.getCache();
	}

	public T getSingle(String key) {
		var nodeCache = resolveNodeCache(key);
		if (nodeCache.isLocal()) {
			return nodeCache.getSingleObject(getName(), key);
		}
		return getRemoteSingle(nodeCache, key);
	}

	protected T getRemoteSingle(NodeCache nodeCache, String key) {
		var near = this.nearCache;
		var cfg = near.getConfig();

		@SuppressWarnings("unchecked")
		var valueClass = (Class<T>)cfg.getValueClass();

		var nearValue = near.getSingle(key);
		long version = (nearValue == null) ? 0 : nearValue.getVersion();

		var r = nodeCache.getSingle(getName(), key, version);

		switch(r.getStatus()) {
			case OK: {
				var valueBytes = r.getValue();
				T valueObj = cfg.getSerder().deserialize(valueBytes, valueClass);
				near.putSingle(key, (k, v) -> new ValueImpl(valueObj, valueBytes, r.getVersion()));
				return valueObj;
			}
			case NO_CHANGE:	return nearValue.getObject(cfg.getSerder(), valueClass);
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
		if (nodeCache.isLocal()) {
			putLocalSingle(nodeCache, key, object);
		} else {
			putRemoteSingle(nodeCache, key, object);
		}
	}

	@SuppressWarnings("unchecked")
	protected void putLocalSingle(NodeCache nodeCache, String key, T object) {
		var valueClass = (Class<T>)this.nearCache.getConfig().getValueClass();
		nodeCache.putSingleObject(getName(), key, object, valueClass);
	}

	protected void putRemoteSingle(NodeCache nodeCache, String key, Object object) {
		var near = this.nearCache;

		var valueBytes = near.getConfig().getSerder().serialize(object);
		long version = nodeCache.putSingle(getName(), key, valueBytes);

		near.putSingle(key, (k, v) -> new ValueImpl(object, valueBytes, version));
	}

}
