package cachemesh.grpc;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cachemesh.Transport;
import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;

@lombok.Getter
//@lombok.NoArgsConstructor
//@lombok.experimental.Builder
public class GrpcConfig implements HasName {

	private final String host;

	private final int port;

	private final int serviceShutdownSeconds;

	private final int clientShutdownSeconds;

	private final String name;

	private final String target;

	public GrpcConfig(String host, int port, int serviceShutdownSeconds, int clientShutdownSeconds) {
		this.host = host;
		this.port = port;
		this.serviceShutdownSeconds = serviceShutdownSeconds;
		this.clientShutdownSeconds = clientShutdownSeconds;

		this.target = String.format("%s:%d", host, port);
		this.name = String.format("%s://%s", Transport.grpc.name(), this.target);
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put("target", getTarget());
		r.put("host", getHost());
		r.put("port", getPort());
		r.put("serviceShutdownSeconds", getServiceShutdownSeconds());
		r.put("clientShutdownSeconds", getClientShutdownSeconds());

		return r;
	}

	public static GrpcConfig from(URL url) {
		String protocol = url.getProtocol();
		if (Transport.valueOf(protocol) == null) {
			throw new InternalException("unsupported meshcache protocol: %s", protocol);
		}

		return new GrpcConfig(url.getHost(), url.getPort(), 30, 30);
	}

}
