package cachemeshpoc.grpc;

import java.net.MalformedURLException;
import java.net.URL;

import cachemeshpoc.MeshProtocol;
import cachemeshpoc.err.MeshInternalException;

@lombok.Getter
////@lombok.NoArgsConstructor
//@lombok.Builder(buildMethodName = "buildConfig")
////@lombok.experimental.SuperBuilder
public class GrpcConfig {

	private final int port;

	private final String host;

	GrpcConfig(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public URL toURL() {
		try {
			return new URL(getTarget());
		} catch (MalformedURLException e) {
			throw new MeshInternalException(e);
		}
	}

	@Override
	public String toString() {
		return toURL().toString();
	}

	public static GrpcConfig from(URL url) {
		String protocol = url.getProtocol();
		if (MeshProtocol.valueOf(protocol) == null) {
			throw new MeshInternalException("unsupported meshcache protocol: %s", protocol);
		}

		return new GrpcConfig(url.getHost(), url.getPort());
	}

	public String getTarget() {
		return String.format("grpc://%s:%d", this.host, this.port);
	}

}
