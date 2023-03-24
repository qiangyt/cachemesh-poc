package cachemesh.spi.base;

import cachemesh.common.hash.Hashing;
import cachemesh.spi.LocalCacheManager;
import cachemesh.spi.NodeCache;

public class SideCache implements NodeCache {

	@lombok.Getter
	private final String name;

	@lombok.Getter
	private final LocalCacheManager<byte[]> localCacheManager;

	@lombok.Getter
	private final Hashing hashing;

	public SideCache(String name, LocalCacheManager<byte[]> localCacheManager, Hashing hashing) {
		this.name = name;
		this.localCacheManager = localCacheManager;
		this.hashing = hashing;
	}

	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var localCache = this.localCacheManager.get(cacheName);
		if (localCache == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		var v = localCache.getSingle(key);
		if (v == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		if (v.getVersion() == version) {
			return (GetResult<byte[]>)GetResult.NO_CHANGE;
		}

		return new GetResult<>(ResultStatus.OK, v.getData(), v.getVersion());
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] valueBytes) {
		long version = this.hashing.hash(valueBytes);//TODO
		var localCache = this.localCacheManager.resolve(cacheName, byte[].class);
		localCache.putSingle(key, new Value<>(valueBytes, version));
		return version;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
	}

}
