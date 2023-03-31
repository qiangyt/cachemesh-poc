package cachemesh.grpc;

import java.util.Map;

import cachemesh.core.Transport;
import cachemesh.core.local.LocalNodeCache;
import cachemesh.spi.NodeCache;
import cachemesh.spi.TransportProvider;
import lombok.Getter;

public class GrpcTransportProvider implements TransportProvider {

	@Getter
	private final GrpcServerManager grpcServerManager;

	@Getter
	private final GrpcClientManager grpcClientManager;

	public GrpcTransportProvider(GrpcServerManager grpcServerManager,
								 GrpcClientManager grpcClientManager) {
		this.grpcServerManager = grpcServerManager;
		this.grpcClientManager = grpcClientManager;
	}

	@Override
	public String getName() {
		return GrpcConfig.PROTOCOL;
	}

	@Override
	public boolean setUpForLocalNode(Map<String,Object> configMap, LocalNodeCache localNodeCache) {
		var service = new GrpcService(localNodeCache);

		var config = GrpcConfig.from(configMap);
		var server = getGrpcServerManager().resolve(config);
		server.addService(service);

		return true;
	}

	@Override
	public NodeCache setUpForRemoteNode(Map<String,Object> configMap) {
		var config = GrpcConfig.from(configMap);
		var client = getGrpcClientManager().resolve(config);
		return new GrpcNodeCache(client);
	}

	@Override
	public Map<String,Object> parseUrl(String url) {
		var transport = Transport.parseUrl(url);
		transport.ensureProtocol(GrpcConfig.PROTOCOL);

		return GrpcConfig.parseName(transport.getTarget());
	}

}
