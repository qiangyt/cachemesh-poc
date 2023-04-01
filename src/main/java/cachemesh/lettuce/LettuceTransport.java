package cachemesh.lettuce;

import java.util.Map;

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.TransportURL;
import cachemesh.spi.NodeCache;
import cachemesh.spi.Transport;
import lombok.Getter;

@Getter
public class LettuceTransport implements Transport {

	private final ShutdownManager shutdownManager;

	public LettuceTransport(ShutdownManager shutdownManager) {
		this.shutdownManager = shutdownManager;
	}

	@Override
	public String getName() {
		return "redis";
	}

	@Override
	public NodeCache createRemoteCache(Map<String,Object> configMap) {
		var config = LettuceConfig.from(configMap);
		return new LettuceCache(config, getShutdownManager());
	}

	@Override
	public Map<String,Object> parseUrl(String url) {
		var transport = TransportURL.parseUrl(url);
		transport.ensureProtocol(LettuceConfig.PROTOCOL);

		return LettuceConfig.parseTarget(transport.getTarget());
	}

}
