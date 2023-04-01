package cachemesh.grpc;

import java.util.Map;

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.LocalNodeCache;
import cachemesh.core.TransportURL;
import cachemesh.spi.NodeCache;
import cachemesh.spi.Transport;
import lombok.Getter;

@Getter
public class GrpcTransport implements Transport {

	private final ShutdownManager shutdownManager;

	public GrpcTransport(ShutdownManager shutdownManager) {
		this.shutdownManager = shutdownManager;
	}

	@Override
	public String getName() {
		return GrpcConfig.PROTOCOL;
	}

	@Override
	public boolean setUpForLocalNode(Map<String,Object> configMap, LocalNodeCache localNodeCache) {
		var config = GrpcConfig.from(configMap);
		var server = new GrpcStandaloneServer(config, getShutdownManager());
		var service = new GrpcService(config, server, localNodeCache);

		return true;asfsd
	}

	@Override
	public NodeCache setUpForRemoteNode(Map<String,Object> configMap) {
		var config = GrpcConfig.from(configMap);
		return new GrpcCache(config, getShutdownManager());
	}

	@Override
	public Map<String,Object> parseUrl(String url) {
		var transport = TransportURL.parseUrl(url);
		transport.ensureProtocol(GrpcConfig.PROTOCOL);

		return GrpcConfig.parseTarget(transport.getTarget());
	}

}
