package cachemeshpoc;

import cachemeshpoc.err.CacheMeshInternalException;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.CacheEntry.Head;
import cachemeshpoc.local.CacheEntry.Value;
import cachemeshpoc.util.Hashing;

public class SideCache implements NodeCache {

	@lombok.Getter
	private final String name;

	private final LocalCache<byte[]> local;

	private final Hashing hashing;

	public SideCache(String name, LocalCache<byte[]> local, Hashing hashing) {
		this.name = name;
		this.local = local;
		this.hashing = hashing;
	}

	@Override
	public GetResult getSingle(String key, long versh) {
		var r = this.local.getSingle(new Head(key, versh));

		var s = r.getStatus();
		switch(s) {
			case NOT_FOUND: return GetResult.NOT_FOUND;
			case NO_CHANGE: return GetResult.NO_CHANGE;
			case OK: {
				var v = r.getValue();
				return new GetResult(ResultStatus.OK, v.getData(), v.getVersh());
			}
			default: {
				throw new CacheMeshInternalException("Unexpected local cache result status: %s", s);
			}
		}
	}

	@Override
	public long putSingle(String key, byte[] bytes) {
		long versh = this.hashing.hash(bytes);
		this.local.putSingle(key, new Value<byte[]>(bytes, versh));
		return versh;
	}

}
