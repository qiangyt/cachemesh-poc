package cachemesh.grpc;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.shutdown.ShutdownableConfig;
import cachemesh.common.util.StringHelper;
import cachemesh.core.Transport;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

@lombok.Getter
@lombok.Builder
public class GrpcConfig implements ShutdownableConfig {

	public static final String PROTOCOL = "grpc";

	private final String host;

	private final int port;

	private final String url;

	private final String name;

	private final int shutdownTimeoutSeconds;

	public ManagedChannel createClientChannel() {
		return Grpc.newChannelBuilder(getName(), InsecureChannelCredentials.create()).build();
	}

	@Override
	public Map<String, Object> toMap() {
		var configMap = new HashMap<String, Object>();

		configMap.put("url", getUrl());
		configMap.put("name", getName());
		configMap.put("host", getHost());
		configMap.put("port", getPort());
		configMap.put("shutdownTimeoutSeconds", getShutdownTimeoutSeconds());

		return configMap;
	}

	public static GrpcConfig from(String url) {
		var transport = Transport.parseUrl(url);
		transport.ensureProtocol(PROTOCOL);

		var configMap = parseName(transport.getTarget());
		return from(configMap);
	}

	public static GrpcConfig from(Map<String,Object> configMap) {
		var url = (String)configMap.get("url");
		var host = (String)configMap.get("host");
		var port = (Integer)configMap.get("port");

		String name;

		if (StringHelper.isBlank(url)) {
			name = formatName(host, port);
			url = Transport.formatUrl(PROTOCOL, name);
		} else {
			if (StringHelper.isBlank(host) == false || port != null) {
				throw new IllegalArgumentException("host+port is exclusive with url");
			}

			var transport = Transport.parseUrl(url);
			transport.ensureProtocol(PROTOCOL);

			name = transport.getTarget();
			configMap = parseName(name);

			host = (String)configMap.get("host");
			port = (Integer)configMap.get("port");
		}

		Integer shutdownTimeoutSeconds = (Integer)configMap.get("shutdownTimeoutSeconds");

		return builder()
				.host(host)
				.port(port.intValue())
				.url(url)
				.name(name)
				.shutdownTimeoutSeconds(shutdownTimeoutSeconds)
				.build();
	}


	public static String formatName(String host, int port) {
		return host + ":" + port;
	}


	public static Map<String,Object> parseName(String name) {
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
