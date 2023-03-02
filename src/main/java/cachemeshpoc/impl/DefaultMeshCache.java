package cachemeshpoc.impl;

import cachemeshpoc.MeshCache;
import cachemeshpoc.MeshRouter;
import cachemeshpoc.Serderializer;
import cachemeshpoc.err.CacheMeshServiceException;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.EntryValue;
import cachemeshpoc.local.caffeine.CaffeineLocalCacheBuilder;

public class DefaultMeshCache<V> implements MeshCache<V> {

	@lombok.Getter
	private final MeshCache.Config<V> config;

	private final LocalCache nodeCache;

	private final MeshRouter router;

	private final Serderializer serder;

	public DefaultMeshCache(MeshCache.Config<V> config, MeshRouter router, Serderializer serder) {
		this.config = config;
		this.nodeCache = new CaffeineLocalCacheBuilder().build(config.getName());
		this.nearCache = new CaffeineLocalCacheBuilder().build(config.getName());
		this.router = router;
		this.serder = serder;
	}

	@Override
	public V getSingle(String key) {
		var node = this.router.findNode(key);
		if (this.router.isSelfNode(node)) {
			var nodeValue = this.nodeCache.getSingle(key);
			if (nodeValue == null) {
				return null;
			}
			return nodeValue.getObject(this.serder, this.config.getValueClass());
		}

		var nearValue = this.nearCache.getSingle(key);
		long ver;
		if (nearValue == null) {
			ver = 0;
		} else {
			ver = nearValue.getVersion();
		}

		var resp = node.getRemoteCache().resolveSingle(this.config.getName(), key, ver);

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
