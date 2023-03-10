package cachemeshpoc;

import cachemeshpoc.err.MeshInternalException;
import cachemeshpoc.err.MeshServiceException;
import cachemeshpoc.local.LocalCache;

public class MeshCache<T> {

	@lombok.Getter
	private final Class<T> valueClass;

	private final LocalCache<VersionedValue> nearCache;

	private final MeshNetwork network;

	@lombok.Getter
	private final Serderializer serder;

	public MeshCache(Class<T> valueClass, LocalCache<VersionedValue> nearCache, MeshNetwork network, Serderializer serder) {
		this.valueClass = valueClass;
		this.nearCache = nearCache;
		this.network = network;
		this.serder = serder;
	}

	public String getName() {
		return this.nearCache.getName();
	}

	@SuppressWarnings("unchecked")
	public T getSingle(String key) {

		var nearValue = this.nearCache.getSingle(key);
		long version = (nearValue == null) ? 0 : nearValue.getVersion();

		var nodeCache = this.network.resolveNodeCache(getName(), key);
		var r = nodeCache.getSingle(key, version);

		switch(r.getStatus()) {
			case OK: {
				var obj = this.serder.deserialize(r.getValue(), this.valueClass);
				this.nearCache.putSingle(key, new VersionedValue(obj, r.getVersion()));
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
		var nodeCache = this.network.resolveNodeCache(getName(), key);

		var bytes = this.serder.serialize(object);
		long version = nodeCache.putSingle(key, bytes);
		this.nearCache.putSingle(key, new VersionedValue(object, version));
	}

}
