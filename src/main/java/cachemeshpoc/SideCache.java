package cachemeshpoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.Entry.Head;
import cachemeshpoc.MeshResult.Status;
import cachemeshpoc.err.CacheMeshInternalException;

public class SideCache implements RawCache {

	private static final Logger LOG = LoggerFactory.getLogger(SideCache.class);

	private final LocalCache.Manager localCaches;

	public SideCache(LocalCache.Manager localCaches) {
		this.localCaches = localCaches;
	}

	@Override
	public void close() throws Exception {
		this.localCaches.close();
	}

	@Override
	public MeshResult resolveSingle(String cacheName, String key, long version) {
		var localCache = this.localCaches.get(cacheName);
		if (localCache == null) {
			return MeshResult.NOT_FOUND;
		}

		var localResult = localCache.resolveSingle(new Head(key, version));

		switch(localResult.getStatus()) {
			case NOT_FOUND: return MeshResult.NOT_FOUND;
			case NO_CHANGE: return MeshResult.NO_CHANGE;
			case CHANGED: {
				var value = localResult.getValue();
				return new MeshResult(Status.OK, (byte[])value.getData(), value.getVersion());
			}
			default: {
				throw new CacheMeshInternalException("Unexpected local cache result status: %s", localResult.getStatus());
			}
		}
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] bytes) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'putSingle'");
	}

}
