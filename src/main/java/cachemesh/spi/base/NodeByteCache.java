package cachemesh.spi.base;

import cachemesh.spi.ByteCache;
import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import cachemesh.spi.LocalCacheManager;
import cachemesh.spi.NodeCache;
import cachemesh.spi.NodeCacheConfig;

@lombok.Getter
public class NodeByteCache implements NodeCache {

	private final LocalCacheManager backend;

	public NodeByteCache(LocalCacheManager backend) {
		this.backend = backend;
	}

	@Override
	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var cache = this.backend.get(cacheName);
		if (cache == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		var v = cache.getSingle(key, version);
		if (v == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		long dataVer = v.getVersion();
		if (dataVer == version) {
			return (GetResult<byte[]>)GetResult.NO_CHANGE;
		}

		var data = v.getData();
		if (data == null) {
			return new GetResult<>(ResultStatus.OK, null, dataVer);
		}

		var dataClass = data.getClass();
		if (dataClass == byte[].class) {
			return new GetResult<>(ResultStatus.OK, (byte[])data, dataVer);
		}

		var cfg = cache.getConfig();

		if (cfg.isCacheBytes()) {
			var cv = (CombinedValue)v;
			var dataBytes = cv.getBytes();
			if (dataBytes == null) {
				dataBytes = cfg.getSerder().serialize(data);
				cv.setBytes(dataBytes);
			}
			return new GetResult<>(ResultStatus.OK, dataBytes, dataVer);
		}

		var serder = cfg.getSerder();
		var dataBytes = serder.serialize(data);
		return new GetResult<>(ResultStatus.OK, dataBytes, dataVer);
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		LocalCache cache = this.backend.resolve(cacheName, byte[].class);

		var r = cache.putSingle(key, (k, entry) -> {
			var cfg = cache.getConfig();
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new Value<byte[]>(entry.getData(), version);
		});

		return r.getVersion();
	}

}
