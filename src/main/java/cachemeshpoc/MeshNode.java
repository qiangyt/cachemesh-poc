package cachemeshpoc;

import java.net.MalformedURLException;
import java.net.URL;

import cachemeshpoc.err.CacheMeshInternalException;
import cachemeshpoc.err.CacheMeshServiceException;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.Entry.Value;
import cachemeshpoc.remote.url.Handler;
import cachemeshpoc.util.ConsistentHash;

@lombok.Getter
public class MeshNode implements ConsistentHash.Node, AutoCloseable {


	public static enum Protocol {

		//resp3,

		grpc;

		static Protocol Default() {
			return grpc;
		}

	}


	static {
		Handler.registerHandler();
	}

	private final URL url;
	private final String key;
	private final int hashCode;

	private final LocalCache.Manager nearCaches;

	private final RawCache rawCache;


	public MeshNode(URL url, LocalCache.Manager nearCaches, RawCache rawCache) {
		this.url = url;
		this.key = url.toExternalForm();
		this.hashCode = this.key.hashCode();

		this.nearCaches = nearCaches;
		this.rawCache = rawCache;
	}

	public static MeshNode parse(String urlText) throws MalformedURLException {
		var url = new URL(urlText);

		String protocol = url.getProtocol();
		if (Protocol.valueOf(protocol) == null) {
			throw new MalformedURLException("unsupported meshcache protocol: " + protocol);
		}

		return new MeshNode(url, null, null);// TODO
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		MeshNode that;
		try {
			that = (MeshNode)obj;
		} catch (ClassCastException e) {
			return false;
		}

		return this.key.equals(that.key);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return this.key;
	}



	public <V> V getSingle(String cacheName, String key) {
		LocalCache<V> nearCache = this.nearCaches.resolve(cacheName);
		var nearValue = nearCache.getSingle(key);
		long nearVersion = (nearValue == null) ? 0 : nearValue.getVersion();

		var resp = this.rawCache.resolveSingle(cacheName, key, nearVersion);

		switch (resp.getStatus()) {

			case NOT_FOUND: {
				if (nearValue != null) {
					nearCache.invalidateSingle(key);
				}
				return null;
			}

			case NO_CHANGE: {
				return nearValue.getData();
			}

			case REDIRECTED: {
				throw new CacheMeshInternalException("TODO");
			}

			case OK: {
				var cfg = nearCache.getConfig();
				V data = cfg.getSerder().deserialize(resp.getBytes(), cfg.getValueClass());
				nearCache.putSingle(key, new Value<>(data, resp.getVersion()));
				return data;
			}
			default: {
				throw new CacheMeshServiceException("unexpected status: %s", resp.getStatus());
			}
		}
	}


	public <V> void setSingle(String cacheName, String key, V value) {
		var nearCache = this.nearCaches.resolve(cacheName);

		var bytes = nearCache.getConfig().getSerder().serialize(value);
		long version = this.rawCache.putSingle(cacheName, key, bytes);

		nearCache.putSingle(key, new Value<>(value, version));
	}

	@Override
	public void close() throws Exception {
		try {
			this.rawCache.close();
		} catch (Exception e) {
			//TODO
		}

		try {
			this.nearCaches.close();
		} catch (Exception e) {
			//TODO
		}
	}

}
