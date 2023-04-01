package cachemesh.grpc;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;
import cachemesh.common.util.StringHelper;
import cachemesh.core.TransportURL;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class GrpcConfig implements Mappable {

	public static final String PROTOCOL = "grpc";

	private final String host;

	private final int port;

	private final String url;

	private final String target;

	private final int shutdownTimeoutSeconds;

	public ManagedChannel createClientChannel() {
		return Grpc.newChannelBuilder(getTarget(), InsecureChannelCredentials.create()).build();
	}

	@Override
	public Map<String, Object> toMap() {
		var configMap = new HashMap<String, Object>();

		configMap.put("url", getUrl());
		configMap.put("target", getTarget());
		configMap.put("host", getHost());
		configMap.put("port", getPort());
		configMap.put("shutdownTimeoutSeconds", getShutdownTimeoutSeconds());

		return configMap;
	}

	public static GrpcConfig from(String url) {
		var transport = TransportURL.parseUrl(url);
		transport.ensureProtocol(PROTOCOL);

		var configMap = parseTarget(transport.getTarget());
		return from(configMap);
	}

	public static GrpcConfig from(Map<String,Object> configMap) {
		var url = (String)configMap.get("url");
		var host = (String)configMap.get("host");
		var port = (Integer)configMap.get("port");

		String target;

		if (StringHelper.isBlank(url)) {
			target = formatTarget(host, port);
			url = TransportURL.formatUrl(PROTOCOL, target);
		} else {
			if (StringHelper.isBlank(host) == false || port != null) {
				throw new IllegalArgumentException("host+port is exclusive with url");
			}

			var transport = TransportURL.parseUrl(url);
			transport.ensureProtocol(PROTOCOL);

			target = transport.getTarget();
			configMap = parseTarget(target);

			host = (String)configMap.get("host");
			port = (Integer)configMap.get("port");
		}

		Integer shutdownTimeoutSeconds = (Integer)configMap.get("shutdownTimeoutSeconds");

		return builder()
				.host(host)
				.port(port.intValue())
				.url(url)
				.target(target)
				.shutdownTimeoutSeconds(shutdownTimeoutSeconds)
				.build();
	}


	public static String formatTarget(String host, int port) {
		return host + ":" + port;
	}


	public static Map<String,Object> parseTarget(String name) {
		int sep = name.indexOf(":");
		if (sep <= 0) {
			throw new IllegalArgumentException("name should follow the format: <host>:<port>");
		}

		String host = name.substring(0, sep);
		String portS = name.substring(sep + ":".length());
		int port = Integer.parseInt(portS);

		var r = new HashMap<String, Object>();
		r.put("host", host);;
		r.put("port", port);
		return r;
	}

}
