package cachemesh.spi.base;

import cachemesh.spi.ByteCache;
import cachemesh.spi.ByteCacheManager;
import cachemesh.spi.NodeCache;

public class NodeByteCache implements NodeCache {

	@lombok.Getter
	private final ByteCacheManager byteCacheManager;

	public NodeByteCache(ByteCacheManager byteCacheManager) {
		this.byteCacheManager = byteCacheManager;
	}

	@Override
	public String getName() {
		return this.byteCacheManager.getName();
	}

	@Override
	public void close() throws Exception {
		this.byteCacheManager.close();
	}

	ByteCache resolveByteCache(String cacheName) {
		return this.byteCacheManager.resolve(cacheName, byte[].class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var byteCache = resolveByteCache(cacheName);

		var v = byteCache.getSingle(key, version);
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
			long version;
			if (entry != null) {
				version = entry.getVersion() + 1;
			}
			return new Value<byte[]>(entry.getData(), version);
		});

		return r.getVersion();
	}

}
