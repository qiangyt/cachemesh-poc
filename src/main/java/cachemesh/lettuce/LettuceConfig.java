package cachemesh.lettuce;

import java.net.URL;

import cachemesh.Transport;
import cachemesh.common.err.InternalException;

@lombok.Getter
@lombok.Builder
public class LettuceConfig {

	private final String host;

	private final int port;

	private final int database;

	@Override
	public String toString() {
		return getTarget();
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
		return new LettuceConfig(url.getHost(), url.getPort(), database);
	}

	public String getTarget() {
		if (this.database > 0) {
			return String.format("redis://%s:%d/%d", this.host, this.port, this.database);
		}
		return String.format("redis://%s:%d", this.host, this.port);
	}

}
