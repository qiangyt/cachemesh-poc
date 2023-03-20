package cachemesh.side;

import cachemesh.common.Hashing;
import cachemesh.spi.LocalCache;
import cachemesh.spi.NodeCache;
import cachemesh.spi.base.GetResult;
import cachemesh.spi.base.ResultStatus;
import cachemesh.spi.base.Value;

public class SideCache implements NodeCache {

	private final LocalCache<byte[]> local;

	@lombok.Getter
	private final Hashing hashing;

	public SideCache(LocalCache<byte[]> local, Hashing hashing) {
		this.local = local;
		this.hashing = hashing;
	}

	@Override
	public String getName() {
		return this.local.getName();
	}

	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String key, long version) {
		var v = this.local.getSingle(key);
		if (v == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		if (v.getVersion() == version) {
			return (GetResult<byte[]>)GetResult.NO_CHANGE;
		}

		return new GetResult<>(ResultStatus.OK, (byte[])v.getData(), v.getVersion());
	}

	@Override
	public long putSingle(String key, byte[] bytes) {
		long version = this.hashing.hash(bytes);
		this.local.putSingle(key, new Value<>(bytes, version));
		return version;
	}

}
