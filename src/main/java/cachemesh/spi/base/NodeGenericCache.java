package cachemesh.spi.base;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import cachemesh.spi.NodeCache;


@ThreadSafe
public class NodeGenericCache<T, C extends LocalCacheConfig<T>> implements NodeCache {

	private final LocalCache<T,Value<T>,C> backend;

	public NodeGenericCache(LocalCache<T,Value<T>,C> backend) {
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

		var serder = this.backend.getConfig().getSerder();
		var valueBytes = serder.serialize(v.getData());

		return new GetResult<>(ResultStatus.OK, valueBytes, v.getVersion());
	}

	@Override
	public long putSingle(String key, byte[] value) {
		var r = this.backend.putSingle(key, (k, entry) -> {
			long version = (entry == null) ? 1 : entry.getVersion() + 1;

			var cfg = this.backend.getConfig();
			var serder = cfg.getSerder();
			var obj = serder.deserialize(value, cfg.getValueClass());

			var v = new Value<T>(obj, version);
			return v;
		});

		return r.getVersion();
	}



}
