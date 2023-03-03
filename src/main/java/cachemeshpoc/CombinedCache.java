package cachemeshpoc;

import cachemeshpoc.err.CacheMeshInternalException;
import cachemeshpoc.local.LocalCache;

public class CombinedCache<T> {

	@lombok.Getter
	private final Class<T> valueClass;

	private final LocalCache<VershedValue> nearCache;

	private final NodeCache nodeCache;

	@lombok.Getter
	private final Serderializer serder;

	public CombinedCache(Class<T> valueClass, LocalCache<VershedValue> nearCache, NodeCache nodeCache, Serderializer serder) {
		this.valueClass = valueClass;
		this.nearCache = nearCache;
		this.nodeCache = nodeCache;
		this.serder = serder;
	}

	public T getSingle(String key) {
		var nearValue = this.nearCache.getSingle(key);

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
				var obj = this.serder.deserialize(r.getBytes(), this.valueClass);
				nearValue = new VershedValue(obj, r.getVersh());
				this.nearCache.putSingle(key, nearValue);

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
		this.nearCache.putSingle(key, new VershedValue(object, versh));
	}

}
