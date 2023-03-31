package cachemesh.lettuce;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.shutdown.ShutdownableConfig;
import cachemesh.common.util.StringHelper;
import cachemesh.core.Transport;

@lombok.Getter
@lombok.Builder
public class LettuceConfig implements ShutdownableConfig {

	public static final String PROTOCOL = "redis";

	private final String host;

	private final int port;

	private final int database;

	private final String url;

	private final String name;

	public static final String DEFAULT_SEPARATOR = "%";

	private final String keySeparator;

	private final int shutdownTimeoutSeconds;


	@Override
	public Map<String, Object> toMap() {
		var configMap = new HashMap<String, Object>();

		configMap.put("host", getHost());
		configMap.put("port", getPort());
		configMap.put("database", getDatabase());
		configMap.put("name", getName());
		configMap.put("url", getUrl());
		configMap.put("keySeparator", getKeySeparator());
		configMap.put("shutdownTimeoutSeconds", getShutdownTimeoutSeconds());

		return configMap;
	}

	public static LettuceConfig from(String url) {
		var transport = Transport.parseUrl(url);
		transport.ensureProtocol(PROTOCOL);

		var configMap = parseName(transport.getTarget());
		return from(configMap);
	}


	public static LettuceConfig from(Map<String,Object> configMap) {
		var url = (String)configMap.get("url");
		var host = (String)configMap.get("host");
		var port = (Integer)configMap.get("port");
		var database = (Integer)configMap.get("database");

		String name;

		if (StringHelper.isBlank(url)) {
			if (database == null) {
				database = Integer.valueOf(0);
			}

			name = formatName(host, port, database);
			url = Transport.formatUrl(PROTOCOL, name);
		} else {
			if (StringHelper.isBlank(host) == false || port != null || database != null) {
				throw new IllegalArgumentException("host+port+database is exclusive with url");
			}

			var transport = Transport.parseUrl(url);
			transport.ensureProtocol(PROTOCOL);

			name = transport.getTarget();
			configMap = parseName(name);

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

		Integer shutdownTimeoutSeconds = (Integer)configMap.get("shutdownTimeoutSeconds");

		return builder()
				.host(host)
				.port(port.intValue())
				.url(url)
				.database(database.intValue())
				.name(name)
				.keySeparator(keySeparator)
				.shutdownTimeoutSeconds(shutdownTimeoutSeconds)
				.build();
	}


	public static String formatName(String host, int port, int database) {
		if (database > 0) {
			return String.format("%s:%d/%d", host, port, database);
		}
		return String.format("%s:%d", host, port);
	}


	public static Map<String,Object> parseName(String name) {
		int sep1 = name.indexOf(":");
		if (sep1 <= 0) {
			throw new IllegalArgumentException("name should follow the format: <host>:<port>/<database>");
		}

		String host = name.substring(0, sep1);
		String portAndDatabase = name.substring(sep1 + ":".length());

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
