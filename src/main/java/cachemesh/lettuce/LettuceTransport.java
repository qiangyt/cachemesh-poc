package cachemesh.lettuce;

import java.util.Map;

import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.core.TransportURL;
import cachemesh.spi.NodeCache;
import cachemesh.spi.Transport;
import lombok.Getter;

@Getter
public class LettuceTransport implements Transport {

	private final ShutdownSupport shutdownSupport;

	public LettuceTransport(ShutdownSupport shutdownSupport) {
		this.shutdownSupport = shutdownSupport;
	}

	@Override
	public String getName() {
		return "redis";
	}

	@Override
	public NodeCache setUpForRemoteNode(Map<String,Object> configMap) {
		var config = LettuceConfig.from(configMap);
		return new LettuceCache(config, getShutdownSupport());
	}

	@Override
	public Map<String,Object> parseUrl(String url) {
		var transport = TransportURL.parseUrl(url);
		transport.ensureProtocol(LettuceConfig.PROTOCOL);

		return LettuceConfig.parseTarget(transport.getTarget());
	}

}
