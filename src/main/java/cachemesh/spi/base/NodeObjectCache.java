package cachemesh.spi.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.common.Closer;
import cachemesh.common.HasName;
import cachemesh.spi.ByteCache;
import cachemesh.spi.NodeCache;
import cachemesh.spi.ObjectCache;
import cachemesh.spi.ObjectCacheConfig;


@ThreadSafe
public class NodeObjectCache<T> implements NodeCache, ObjectCache<T> {

	private final ByteCache byteCache;

	private final ObjectCache<T> objectCache;

	public NodeObjectCache(ByteCache byteCache, ObjectCache<T> objectCache) {
		this.byteCache = byteCache;
		this.objectCache = objectCache;
	}

	@Override
	public String getName() {
		if (this.byteCache != null) {
			return this.byteCache.getName();
		}
		if (this.objectCache != null) {
			return this.objectCache.getName();
		}
		return "unknown";
	}

	@Override
	public void close() throws Exception {
		Closer.closeSilently(this.byteCache);
		Closer.closeSilently(this.objectCache);
	}

	@Override
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getSingle'");
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		if (this.byteCache != null) {
			return this.byteCache.getName();
		}
	}

	@Override
	public ObjectCacheConfig<T> getConfig() {
		return this.objectCache.getConfig();
	}

	@Override
	public Value<T> getSingle(String key) {
		return this.objectCache.getSingle(key);
	}

	@Override
	public void putSingle(String key, Value<T> value) {
		objectCache.putSingle(key, value);
	}



}
