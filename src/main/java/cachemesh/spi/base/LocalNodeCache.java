package cachemesh.spi.base;

import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheManager;
import cachemesh.spi.NodeCache;

@lombok.Getter
public class LocalNodeCache implements NodeCache {

	private final LocalCacheManager backend;

	public LocalNodeCache(LocalCacheManager backend) {
		this.backend = backend;
	}

	@Override
	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var cache = this.backend.get(cacheName);
		if (cache == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		var v = cache.getSingle(key);
		if (v == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		long dataVer = v.getVersion();
		if (dataVer == version) {
			return (GetResult<byte[]>)GetResult.NO_CHANGE;
		}

		var data = v.getBytes(cache.getConfig().getSerder());
		return new GetResult<>(ResultStatus.OK, data, dataVer);
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		LocalCache cache = this.backend.resolve(cacheName, byte[].class);

		var r = cache.putSingle(key, (k, entry) -> {
			var cfg = cache.getConfig();
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new Value(entry.getBytes(cfg.getSerder()), version);
		});

		return r.getVersion();
	}

}
