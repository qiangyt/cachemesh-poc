package cachemesh.grpc;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cachemesh.Transport;
import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

@lombok.Getter
//@lombok.NoArgsConstructor
//@lombok.experimental.Builder
public class GrpcConfig implements HasName {

	private final String host;

	private final int port;

	private final String name;

	private final String target;

	public GrpcConfig(String host, int port) {
		this.host = host;
		this.port = port;

		this.target = String.format("%s:%d", host, port);
		this.name = String.format("%s://%s", Transport.grpc.name(), this.target);
	}

	public ManagedChannel createClientChannel() {
		return Grpc.newChannelBuilder(getTarget(), InsecureChannelCredentials.create()).build();
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put("target", getTarget());
		r.put("host", getHost());
		r.put("port", getPort());

		return r;
	}

	public static GrpcConfig from(URL url) {
		String protocol = url.getProtocol();
		if (Transport.valueOf(protocol) == null) {
			throw new InternalException("unsupported meshcache protocol: %s", protocol);
		}

		return new GrpcConfig(url.getHost(), url.getPort());
	}

}
