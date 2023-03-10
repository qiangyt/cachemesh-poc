package cachemeshpoc.side;

import cachemeshpoc.GetResult;
import cachemeshpoc.NodeCache;
import cachemeshpoc.ResultStatus;
import cachemeshpoc.VersionedValue;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.util.Hashing;

public class SideCache implements NodeCache {

	private final LocalCache<VersionedValue> local;

	@lombok.Getter
	private final Hashing hashing;

	public SideCache(LocalCache<VersionedValue> local, Hashing hashing) {
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
		this.local.putSingle(key, new VersionedValue(bytes, version));
		return version;
	}

}
