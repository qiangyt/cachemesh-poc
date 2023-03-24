package cachemesh.spi.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.common.Closer;
import cachemesh.common.HasName;
import cachemesh.spi.ByteCache;
import cachemesh.spi.NodeCache;
import cachemesh.spi.ObjectCache;
import cachemesh.spi.ObjectCacheConfig;
import cachemesh.spi.ObjectCacheManager;


@ThreadSafe
public class NodeObjectCache<T> implements NodeCache {

	private final ObjectCacheManager<T> objectCacheManager;

	public NodeObjectCache(ObjectCacheManager<T> objectCacheManager) {
		this.objectCacheManager = objectCacheManager;
	}

	@Override
	public String getName() {
		return this.objectCacheManager.getName();
	}

	@Override
	public void close() throws Exception {
		this.objectCacheManager.close();
	}

	ObjectCache<T> resolveObjectCache(String cacheName, Class<T> valueClass) {
		return this.objectCacheManager.resolve(cacheName, valueClass);
	}

	@Override
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var objCache = resolveObjectCache(cacheName);

		var v = objCache.getSingle(key, version);
		if (v == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		if (v.getVersion() == version) {
			return (GetResult<byte[]>)GetResult.NO_CHANGE;
		}

		return new GetResult<>(ResultStatus.OK, v.getData(), v.getVersion());
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		var byteCache = resolveByteCache(cacheName);

		var r = byteCache.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new Value<byte[]>(entry.getData(), version);
		});

		return r.getVersion();
	}



}
