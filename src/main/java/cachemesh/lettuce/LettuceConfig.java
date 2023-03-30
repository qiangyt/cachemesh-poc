package cachemesh.lettuce;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;
import cachemesh.common.util.StringHelper;
import cachemesh.core.Transport;

@lombok.Getter
@lombok.Builder
public class LettuceConfig implements Mappable {

	public static final String PROTOCOL = "redis";

	private final String host;

	private final int port;

	private final int database;

	private final String url;

	private final String target;

	public static final String DEFAULT_SEPARATOR = "%";

	private final String keySeparator;

	private final LettuceCodec codec;


	@Override
	public Map<String, Object> toMap() {
		var configMap = new HashMap<String, Object>();

		configMap.put("host", getHost());
		configMap.put("port", getPort());
		configMap.put("database", getDatabase());
		configMap.put("target", getTarget());
		configMap.put("url", getUrl());
		configMap.put("keySeparator", getKeySeparator());
		configMap.put("codec", getCodec());

		return configMap;
	}

	public static LettuceConfig from(String url) {
		var transport = Transport.parseUrl(url);
		transport.ensureProtocol(PROTOCOL);

		var configMap = parseTarget(transport.getTarget());
		return from(configMap);
	}


	public static LettuceConfig from(Map<String,Object> configMap) {
		var url = (String)configMap.get("url");
		var host = (String)configMap.get("host");
		var port = (Integer)configMap.get("port");
		var database = (Integer)configMap.get("database");

		String target;

		if (StringHelper.isBlank(url)) {
			if (database == null) {
				database = Integer.valueOf(0);
			}

			target = formatTarget(host, port, database);
			url = Transport.formatUrl(PROTOCOL, target);
		} else {
			if (StringHelper.isBlank(host) == false || port != null || database != null) {
				throw new IllegalArgumentException("host+port+database is exclusive with url");
			}

			var transport = Transport.parseUrl(url);
			transport.ensureProtocol(PROTOCOL);

			target = transport.getTarget();
			configMap = parseTarget(target);

			host = (String)configMap.get("host");
			port = (Integer)configMap.get("port");
			database = (Integer)configMap.get("database");

			if (database == null) {
				database = Integer.valueOf(0);
			}
		}

		var keySeparator = (String)configMap.get("keySeparator");
		if (StringHelper.isBlank(keySeparator)) {
			keySeparator = DEFAULT_SEPARATOR;
		}

		var codec = (LettuceCodec)configMap.get("codec");
		if (codec == null) {
			codec = LettuceCodec.DEFAULT;
		}

		return builder()
				.host(host)
				.port(port.intValue())
				.url(url)
				.database(database.intValue())
				.target(target)
				.keySeparator(keySeparator)
				.codec(codec)
				.build();
	}


	public static String formatTarget(String host, int port, int database) {
		if (database > 0) {
			return String.format("%s:%d/%d", host, port, database);
		}
		return String.format("%s:%d", host, port);
	}


	public static Map<String,Object> parseTarget(String target) {
		int sep1 = target.indexOf(":");
		if (sep1 <= 0) {
			throw new IllegalArgumentException("target should follow the format: <host>:<port>/<database>");
		}

		String host = target.substring(0, sep1);
		String portAndDatabase = target.substring(sep1 + ":".length());

		int port;
		int database;
		int sep2 = portAndDatabase.indexOf("/");
		if (sep2 <= 0) {
			port = Integer.parseInt(portAndDatabase);
			database = 0;
		} else {
			String portS = portAndDatabase.substring(0, sep2);
			port = Integer.parseInt(portS);

			String databaseS = portAndDatabase.substring(sep2 + "/".length());
			database = Integer.parseInt(databaseS);
		}

		var r = new HashMap<String, Object>();
		r.put("host", host);
		r.put("port", port);
		r.put("database", database);
		return r;
	}

}
