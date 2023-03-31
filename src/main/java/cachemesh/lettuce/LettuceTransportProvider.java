package cachemesh.lettuce;

import java.util.Map;

import cachemesh.common.shutdown.ShutdownableManager;
import cachemesh.core.Transport;
import cachemesh.spi.NodeCache;
import cachemesh.spi.TransportProvider;
import lombok.Getter;

public class LettuceTransportProvider
	extends ShutdownableManager<LettuceNodeCache, LettuceConfig>
	implements TransportProvider {

	@Getter
	private final LettuceChannelManager lettuceChannelManager;

	public LettuceChannelManager(String name, ShutdownSupport shutdownSupport) {
		super(name, shutdownSupport, 0);
	}

	public LettuceChannelManager(String name, ShutdownSupport shutdownSupport, int shutdownTimeoutSeconds) {
		super(name, shutdownSupport, shutdownTimeoutSeconds);
	}

	@Override
	public String getProtocol() {
		return "redis";
	}

	@Override
	public NodeCache setUpForRemoteNode(Map<String,Object> configMap) {
		var config = LettuceConfig.from(configMap);
		var channel = getLettuceChannelManager().resolve(config);
		return new LettuceNodeCache(channel);
	}

	@Override
	public Map<String,Object> parseUrl(String url) {
		var transport = Transport.parseUrl(url);
		transport.ensureProtocol(LettuceConfig.PROTOCOL);

		return LettuceConfig.parseName(transport.getTarget());
	}

}
