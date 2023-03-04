package cachemeshpoc;

import cachemeshpoc.err.MeshInternalException;
import cachemeshpoc.err.MeshServiceException;
import cachemeshpoc.local.LocalCache;

public class Cache<T> {

	@lombok.Getter
	private final Class<T> valueClass;

	private final LocalCache<VershedValue> nearCache;

	private final Network net;

	@lombok.Getter
	private final Serderializer serder;

	public Cache(Class<T> valueClass, LocalCache<VershedValue> nearCache, Network net, Serderializer serder) {
		this.valueClass = valueClass;
		this.nearCache = nearCache;
		this.net = net;
		this.serder = serder;
	}

	public String getName() {
		return this.nearCache.getName();
	}

	@SuppressWarnings("unchecked")
	public T getSingle(String key) {
		var nearValue = this.nearCache.getSingle(key);

		long versh;
		if (nearValue != null) {
			versh = nearValue.getVersh();
		} else {
			versh = 0;
		}

		var node = this.net.findNode(key);
		var nodeCacheMgr = node.getNodeCacheManager();
		var nodeCache = nodeCacheMgr.resolve(getName());

		var r = nodeCache.getSingle(key, versh);

		switch(r.getStatus()) {
			case OK: {
				var obj = this.serder.deserialize(r.getValue(), this.valueClass);
				nearValue = new VershedValue(obj, r.getVersh());
				this.nearCache.putSingle(key, nearValue);

				return obj;
			}
			case NO_CHANGE:	return (T)nearValue.getData();
			case NOT_FOUND: {
				this.nearCache.invalidateSingle(key);
				return null;
			}
			case REDIRECT: {
				this.nearCache.invalidateSingle(key);
				throw new MeshInternalException("TODO");
			}
			default: throw new MeshServiceException("unexpected status");
		}
	}

	public void putSingle(String key, T object) {
		var bytes = this.serder.serialize(object);

		var node = this.net.findNode(key);
		var nodeCacheMgr = node.getNodeCacheManager();
		var nodeCache = nodeCacheMgr.resolve(getName());

		long versh = nodeCache.putSingle(key, bytes);

		this.nearCache.putSingle(key, new VershedValue(object, versh));
	}

}
