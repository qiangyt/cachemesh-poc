package cachemesh.core;

import cachemesh.spi.LocalCache;
import cachemesh.spi.Transport;
import lombok.Getter;

@Getter
public class LocalTransport implements Transport {

	private final LocalCacheManager localCacheManager;

	public LocalTransport(LocalCacheManager localCacheManager) {
		this.localCacheManager = localCacheManager;
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public void start(int timeoutSeconds) throws InterruptedException {
		// nothing to do
	}

	@Override
	public void stop(int timeoutSeconds) throws InterruptedException {
		// nothing to do
	}

	@Override
	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var cache = getLocalCacheManager().get(cacheName);
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
		LocalCache cache = getLocalCacheManager().resolve(cacheName, byte[].class);

		var r = cache.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new ValueImpl(value, version);
		});

		return r.getVersion();
	}

	@Override
	public <T> T getSingleObject(String cacheName, String key) {
		var cache = getLocalCacheManager().get(cacheName);
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
		LocalCache cache = getLocalCacheManager().resolve(cacheName, valueClass);

		var r = cache.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new ValueImpl(value, version);
		});

		return r.getVersion();
	}

}
