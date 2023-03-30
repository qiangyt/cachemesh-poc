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
		if (v == null || v.hasValue() == false) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		long dataVer = v.getVersion();
		if (dataVer == version) {
			return (GetResult<byte[]>)GetResult.NO_CHANGE;
		}

		var data = v.isNullValue() ? null : v.getBytes(cache.getConfig().getSerder());
		return new GetResult<>(ResultStatus.OK, data, dataVer);
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		LocalCache cache = this.backend.resolve(cacheName, byte[].class);

		var r = cache.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new ValueImpl(value, version);
		});

		return r.getVersion();
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public <T> T getSingleObject(String cacheName, String key) {
		var cache = this.backend.get(cacheName);
		if (cache == null) {
			return null;
		}

		var v = cache.getSingle(key);
		if (v == null || v.hasValue() == false) {
			return null;
		}

		var cfg = cache.getConfig();
		return v.isNullValue() ? null : v.getObject(cfg.getSerder(), cfg.getValueClass());
	}

	@Override
	public <T> long putSingleObject(String cacheName, String key, T value, Class<T> valueClass) {
		LocalCache cache = this.backend.resolve(cacheName, valueClass);

		var r = cache.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new ValueImpl(value, version);
		});

		return r.getVersion();
	}

}
