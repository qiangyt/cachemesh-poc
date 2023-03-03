package cachemeshpoc;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.util.Hashing;

public class SideCache implements NodeCache {

	private final LocalCache<VershedValue> local;

	@lombok.Getter
	private final Hashing hashing;

	public SideCache(LocalCache<VershedValue> local, Hashing hashing) {
		this.local = local;
		this.hashing = hashing;
	}

	@Override
	public String getName() {
		return this.local.getName();
	}

	public GetResult getSingle(String key, long versh) {
		var v = this.local.getSingle(key);
		if (v == null) {
			return GetResult.NOT_FOUND;
		}

		if (v.getVersh() == versh) {
			return GetResult.NO_CHANGE;
		}

		return new GetResult(ResultStatus.OK, (byte[])v.getData(), v.getVersh());
	}

	@Override
	public long putSingle(String key, byte[] bytes) {
		long versh = this.hashing.hash(bytes);
		this.local.putSingle(key, new VershedValue(bytes, versh));
		return versh;
	}

}
