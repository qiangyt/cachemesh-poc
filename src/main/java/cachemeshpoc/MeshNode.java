package cachemeshpoc;

import java.net.MalformedURLException;
import java.net.URL;

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

	private final CombinedCacheManager cacheManager;


	public MeshNode(URL url, CombinedCacheManager cacheManager) {
		this.url = url;
		this.key = url.toExternalForm();
		this.hashCode = this.key.hashCode();

		this.cacheManager = cacheManager;
	}

	public static MeshNode parse(String urlText) throws MalformedURLException {
		var url = new URL(urlText);

		String protocol = url.getProtocol();
		if (Protocol.valueOf(protocol) == null) {
			throw new MalformedURLException("unsupported meshcache protocol: " + protocol);
		}

		return new MeshNode(url, null);// TODO
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

	@SuppressWarnings("unchecked")
	public <V> V getSingle(String cacheName, String key) {
		var cache = this.cacheManager.resolve(cacheName, null);
		return (V)cache.getSingle(key);
	}


	public <V> void setSingle(String cacheName, String key, V value) {
		var cache = this.cacheManager.resolve(cacheName, null);
		cache.putSingle(key, value);
	}

	@Override
	public void close() throws Exception {
		this.cacheManager.close();
	}

}
