package cachemesh.spi.base;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import cachemesh.spi.NodeCache;


@ThreadSafe
public class NodeCombinedCache<T, C extends LocalCacheConfig<T>> implements NodeCache {

	private final LocalCache<T, CombinedValue<T>, C> backend;

	public NodeCombinedCache(LocalCache<T, CombinedValue<T>, C> backend) {
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

		var valueBytes = v.getBytes();
		if (valueBytes == null) {
			var serder = this.backend.getConfig().getSerder();
			valueBytes = serder.serialize(v.getData());
			v.setBytes(valueBytes);
		}

		return new GetResult<>(ResultStatus.OK, valueBytes, v.getVersion());
	}

	@Override
	public long putSingle(String key, byte[] value) {
		var r = this.backend.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;

			var cfg = this.backend.getConfig();
			var serder = cfg.getSerder();
			var obj = serder.deserialize(value, cfg.getValueClass());

			var v = new CombinedValue<T>(obj, version);
			return v;
		});

		return r.getVersion();
	}



}
