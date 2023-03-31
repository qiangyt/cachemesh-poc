package cachemesh.lettuce;

import java.util.Map;

import cachemesh.core.Transport;
import cachemesh.spi.NodeCache;
import cachemesh.spi.TransportProvider;
import lombok.Getter;

public class LettuceTransportProvider implements TransportProvider {

	@Getter
	private final LettuceChannelManager lettuceChannelManager;

	public LettuceTransportProvider(LettuceChannelManager lettuceChannelManager) {
		this.lettuceChannelManager = lettuceChannelManager;
	}

	@Override
	public String getName() {
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
