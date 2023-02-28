package qiangyt.cachemeshpoc.impl;

import qiangyt.cachemeshpoc.MeshCache;
import qiangyt.cachemeshpoc.local.LocalCache;
import qiangyt.cachemeshpoc.local.LocalEntry;
import qiangyt.cachemeshpoc.local.caffeine.CaffeineLocalCacheBuilder;
import qiangyt.cachemeshpoc.route.MeshRouter;
import qiangyt.cachemeshpoc.serde.Serderializer;

public class DefaultMeshCache<V> implements MeshCache<V> {

	@lombok.Getter
	private final String name;

	@lombok.Getter
	private final Class<V> valueClass;

	private final LocalCache<V> localCache;

	private final MeshRouter router;

	private final Serderializer serder;

	public DefaultMeshCache(String name, Class<V> valueClass, MeshRouter router, Serderializer serder) {
		this.name = name;
		this.valueClass = valueClass;
		this.localCache = new CaffeineLocalCacheBuilder().build(name, valueClass);
		this.router = router;
		this.serder = serder;
	}

	@Override
	public V getSingle(String key) {
		var localEntry = this.localCache.getSingle(key);

		var node = this.router.findNode(key);
		if (this.router.isSelfNode(node)) {
			if (localEntry == null) {
				return null;
			}
			return localEntry.getValue();
		}

		long version;
		if (localEntry == null) {
			version = 0;
		} else {
			version = localEntry.getVersion();
		}

		var remote = node.getRemoteCache();
		var resp = remote.getSingle(this.name, key, version);

		switch (resp.getStatus()) {
			case Changed: {
				return localEntry.getValue();
			}
			case NoChange: {
				return localEntry.getValue();
			}
			case NotFound: {
				return null;
			}
			default: {
				throw new IllegalStateException("unexpected status: " + resp.getStatus());
			}
		}
	}

	@Override
	public void setSingle(String key, V value) {
		var node = this.router.findNode(key);
		if (this.router.isSelfNode(node)) {
			var entry = new LocalEntry<V>();
			entry.setKey(key);
			entry.setValue(value);
			this.localCache.setSingle(entry);
			return;
		}

		var remote = node.getRemoteCache();
		remote.setSingle(this.name, key, serder.serialize(value));
	}

	@Override
	public void close() throws Exception {
		this.localCache.close();
	}

}
