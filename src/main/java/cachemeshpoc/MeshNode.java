package cachemeshpoc;

import java.net.MalformedURLException;
import java.net.URL;

import cachemeshpoc.remote.grpc.url.Handler;

@lombok.Getter
public class MeshNode {

	static {
		Handler.registerHandler();
	}

	private final URL url;
	private final String key;
	private final int hashCode;


	public static MeshNode parse(String urlText) throws MalformedURLException {
		var url = new URL(urlText);

		String protocol = url.getProtocol();
		if (MeshProtocol.valueOf(protocol)==null) {
			throw new MalformedURLException("unsupported meshcache protocol: " + protocol);
		}

		return new MeshNode(url);
	}

	protected MeshNode(URL url) {
		this.url = url;
		this.key = url.toExternalForm();
		this.hashCode = this.key.hashCode();
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

}
