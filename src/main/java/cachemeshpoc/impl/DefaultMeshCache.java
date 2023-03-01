package cachemeshpoc.impl;

import cachemeshpoc.MeshCache;
import cachemeshpoc.err.CacheMeshServiceException;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.EntryValue;
import cachemeshpoc.local.caffeine.CaffeineLocalCacheBuilder;
import cachemeshpoc.route.MeshRouter;
import cachemeshpoc.serde.Serderializer;

public class DefaultMeshCache<V> implements MeshCache<V> {

	@lombok.Getter
	private final String name;

	@lombok.Getter
	private final Class<V> valueClass;

	private final LocalCache localCache;

	private final MeshRouter router;

	private final Serderializer serder;

	public DefaultMeshCache(String name, Class<V> valueClass, MeshRouter router, Serderializer serder) {
		this.name = name;
		this.valueClass = valueClass;
		this.localCache = new CaffeineLocalCacheBuilder().build(name);
		this.router = router;
		this.serder = serder;
	}

	@Override
	public V getSingle(String key) {
		var localValue = this.localCache.getSingle(key);

		var node = this.router.findNode(key);
		if (this.router.isSelfNode(node)) {
			if (localValue == null) {
				return null;
			}

			return localValue.getObject(this.serder, this.valueClass);
		}

		long ver;
		if (localValue == null) {
			ver = 0;
		} else {
			ver = localValue.getVersion();
		}

		var resp = node.getRemoteCache().resolveSingle(this.name, key, ver);

		switch (resp.getStatus()) {
			case Changed: {
				localValue = EntryValue.builder().bytes(resp.getValue()).version(resp.getVersion()).build();
				this.localCache.putSingle(key, localValue);
				return localValue.getObject(this.serder, this.valueClass);
			}
			case NoChange: {
				return localValue.getObject(this.serder, this.valueClass);
			}
			case NotFound: {
				return null;
			}
			default: {
				throw new CacheMeshServiceException("unexpected status: %s", resp.getStatus());
			}
		}
	}

	@Override
	public void setSingle(String key, V obj) {
		var localValue = this.localCache.getSingle(key);

		var node = this.router.findNode(key);
		if (this.router.isSelfNode(node)) {
			long ver = (localValue == null) ? 0 : localValue.getVersion();
			localValue = EntryValue.builder().object(obj).version(ver).build();
			this.localCache.putSingle(key, localValue);
			return;
		}

		localValue = EntryValue.builder().object(obj).build();
		long ver = node.getRemoteCache().putSingle(this.name, key, localValue.getBytes(this.serder));
		localValue.setVersion(ver);
		this.localCache.putSingle(key, localValue);
	}

	@Override
	public void close() throws Exception {
		this.localCache.close();
	}

}
