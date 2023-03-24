package cachemesh.lettuce;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cachemesh.Transport;
import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;

@lombok.Getter
public class LettuceConfig implements HasName {

	private final String name;

	private final String target;

	private final String host;

	private final int port;

	private final int database;

	public static final String DEFAULT_SEPARATOR = "%";

	private final String separator;

	private final LettuceCodec codec;

	public LettuceConfig(String host, int port, int database, String separator) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.codec = LettuceCodec.DEFAULT;
		this.separator = separator;

		if (database > 0) {
			this.target = String.format("%s:%d/%d", this.host, this.port, this.database);
		} else {
			this.target = String.format("%s:%d", this.host, this.port);
		}

		this.name = String.format("%s://%s", Transport.redis.name(), this.target);
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put("target", getTarget());
		r.put("host", getHost());
		r.put("port", getPort());
		r.put("database", getDatabase());
		r.put("separator", getSeparator());

		return r;
	}


	public static LettuceConfig from(URL url) {
		String protocol = url.getProtocol();
		if (Transport.valueOf(protocol) == null) {
			throw new InternalException("unsupported meshcache protocol: %s", protocol);
		}

		int database;
		if (url.getPath().isEmpty()) {
			database = 0;
		} else {
			database = Integer.valueOf(url.getPath());
		}
		return new LettuceConfig(url.getHost(), url.getPort(), database, DEFAULT_SEPARATOR);
	}

}
