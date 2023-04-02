package cachemesh.core;

import org.slf4j.Logger;
import lombok.Getter;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.err.ServiceException;
import cachemesh.common.util.LogHelper;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.Transport;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Getter
public class MeshCache<T> implements HasName {

	private final Logger logger;

	private final LocalCache nearCache;

	private final MeshNetwork network;

	public MeshCache(String name, LocalCacheManager nearCacheManager, MeshNetwork network) {
		this.nearCache = nearCacheManager.get(name);
		this.network = network;

		this.logger = LogHelper.getLogger(this);
	}

	@Override
	public String getName() {
		return this.nearCache.getName();
	}

	public Transport resolveTransport(String key) {
		var n = getNetwork().findNode(key);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("find node for {}: {}", kv("key", key), LogHelper.kv("node", n));
		}

		return n.getTransport();
	}

	public T getSingle(String key) {
		var transport = resolveTransport(key);
		if (transport.isRemote() == false) {
			return transport.getSingleObject(getName(), key);
		}
		return getRemoteSingle(transport, key);
	}

	protected T getRemoteSingle(Transport nodeCache, String key) {
		var near = getNearCache();
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
				near.invalidateSingle(key);
				return null;
			}
			case REDIRECT: {
				near.invalidateSingle(key);
				throw new InternalException("TODO");
			}
			default: throw new ServiceException("unexpected status");
		}
	}

	public void putSingle(String key, T object) {
		var transport = resolveTransport(key);
		if (transport.isRemote() == false) {
			putLocalSingle(transport, key, object);
		} else {
			putRemoteSingle(transport, key, object);
		}
	}

	@SuppressWarnings("unchecked")
	protected void putLocalSingle(Transport nodeCache, String key, T object) {
		var near = getNearCache();

		var valueClass = (Class<T>)near.getConfig().getValueClass();
		nodeCache.putSingleObject(getName(), key, object, valueClass);
	}

	protected void putRemoteSingle(Transport nodeCache, String key, Object object) {
		var near = getNearCache();

		var valueBytes = near.getConfig().getSerder().serialize(object);
		long version = nodeCache.putSingle(getName(), key, valueBytes);

		near.putSingle(key, (k, v) -> new ValueImpl(object, valueBytes, version));
	}

}
