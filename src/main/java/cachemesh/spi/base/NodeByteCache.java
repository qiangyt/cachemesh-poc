package cachemesh.spi.base;

import cachemesh.spi.ByteCache;
import cachemesh.spi.NodeCache;

public class NodeByteCache implements NodeCache {

	private final ByteCache backend;

	public NodeByteCache(ByteCache backend) {
		this.backend = backend;
	}

	@Override
	public String getName() {
		return this.backend.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String key, long version) {
		var v = this.backend.getSingle(key, version);
		if (v == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		if (v.getVersion() == version) {
			return (GetResult<byte[]>)GetResult.NO_CHANGE;
		}

		return new GetResult<>(ResultStatus.OK, v.getData(), v.getVersion());
	}

	@Override
	public long putSingle(String key, byte[] value) {
		var r = this.backend.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;
			return new Value<byte[]>(entry.getData(), version);
		});

		return r.getVersion();
	}

}
