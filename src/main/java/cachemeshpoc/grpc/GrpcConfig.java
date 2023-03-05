package cachemeshpoc.grpc;

import java.net.URL;

import cachemeshpoc.Protocol;
import cachemeshpoc.err.MeshInternalException;

@lombok.Getter
@lombok.Builder
//@lombok.NoArgsConstructor
//@lombok.experimental.Builder
public class GrpcConfig {

	private final String host;

	private final int port;

	private final int serviceShutdownSeconds;

	private final int clientShutdownSeconds;

	@Override
	public String toString() {
		return String.format("grpc://%s:%d", this.host, this.port);
	}

	public static GrpcConfig from(URL url) {
		String protocol = url.getProtocol();
		if (Protocol.valueOf(protocol) == null) {
			throw new MeshInternalException("unsupported meshcache protocol: %s", protocol);
		}

		return new GrpcConfig(url.getHost(), url.getPort(), 30, 30);
	}

	public String getTarget() {
		return String.format("%s:%d", this.host, this.port);
	}

}
