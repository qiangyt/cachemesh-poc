package cachemeshpoc;

import cachemeshpoc.GetResult.Status;
import cachemeshpoc.err.CacheMeshInternalException;
import cachemeshpoc.generic.GenericCache;
import cachemeshpoc.generic.GenericEntry.Head;
import cachemeshpoc.generic.GenericEntry.Value;
import cachemeshpoc.util.Hashing;

public class SideCache implements NodeCache {

	@lombok.Getter
	private final String name;

	private final GenericCache<byte[]> local;

	private final Hashing hashing;

	public SideCache(String name, GenericCache<byte[]> local, Hashing hashing) {
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
				return new GetResult(Status.OK, v.getData(), v.getVersh());
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
