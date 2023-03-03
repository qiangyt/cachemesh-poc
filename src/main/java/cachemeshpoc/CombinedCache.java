package cachemeshpoc;

import cachemeshpoc.err.CacheMeshInternalException;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.CacheEntry.Value;

public class CombinedCache<T> {

	private final LocalCache<T> nearCache;

	private final NodeCache nodeCache;

	private final Serderializer serder;

	public CombinedCache(LocalCache<T> nearCache, NodeCache nodeCache, Serderializer serder) {
		this.nearCache = nearCache;
		this.nodeCache = nodeCache;
		this.serder = serder;
	}

	public T getSingle(String key) {
		var nearValue = this.nearCache.getSingleAnyhow(key);

		long versh;
		if (nearValue != null) {
			versh = nearValue.getVersh();
		} else {
			versh = 0;
		}

		var r = this.nodeCache.getSingle(key, versh);
		switch (r.getStatus()) {
			case NOT_FOUND: {
				this.nearCache.invalidateSingle(key);
				return null;
			}
			case REDIRECT: {
				throw new CacheMeshInternalException("TODO");
			}
			case OK: {
				var obj = this.serder.deserialize(r.getBytes(), this.nearCache.getValueClass());
				this.nearCache.putSingle(key, new Value<T>(obj, r.getVersh()));
				return obj;
			}
			default: {
				throw new CacheMeshInternalException("TODO");
			}
		}
	}

	public void putSingle(String key, T object) {
		var bytes = this.serder.serialize(object);
		long versh = this.nodeCache.putSingle(key, bytes);
		this.nearCache.putSingle(key, new Value<T>(object, versh));
	}

}
