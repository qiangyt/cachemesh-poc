package cachemesh.grpc;

import java.util.Map;

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.LocalTransport;
import cachemesh.core.MeshNode;
import cachemesh.core.TransportConfig;
import cachemesh.core.TransportURL;
import cachemesh.spi.Transport;
import cachemesh.spi.TransportProvider;
import lombok.Getter;

@Getter
public class GrpcTransportProvider implements TransportProvider {

	private final GrpcServerProvider serverProvider;

	private final ShutdownManager shutdownManager;

	public GrpcTransportProvider(GrpcServerProvider serverProvider, ShutdownManager shutdownManager) {
		this.serverProvider = serverProvider;
		this.shutdownManager = shutdownManager;
	}

	@Override
	public void beforeNodeStart(MeshNode node, int timeoutSeconds) throws InterruptedException {
		var cfg = (GrpcConfig)node.getConfig();
		if (cfg.isRemote()) {
			return;
		}

		var server = getServerProvider().get(cfg);
		server.start(timeoutSeconds);
	}

	@Override
	public boolean setUpLocalTransport(TransportConfig transportConfig, LocalTransport localTranport) {
		var config = (GrpcConfig)transportConfig;
		var server = new DedicatedGrpcServer(config, getShutdownManager());

		if (server.isStarted()) {
			throw new IllegalStateException("should NOT add service after grpc server is started");
		}
		var service = new GrpcService(config, localTranport);
		server.addService(service);

		return true;
	}

	@Override
	public String getProtocol() {
		return GrpcConfig.PROTOCOL;
	}

	@Override
	public TransportConfig parseConfig(Map<String,Object> configMap) {
		return GrpcConfig.from(configMap);
	}

	@Override
	public Transport createRemoteTransport(TransportConfig transportConfig) {
		var config = (GrpcConfig)transportConfig;
		return new GrpcTransport(config, getShutdownManager());
	}

	@Override
	public Map<String,Object> parseUrl(String url) {
		var transport = TransportURL.parseUrl(url);
		transport.ensureProtocol(GrpcConfig.PROTOCOL);

		return GrpcConfig.parseTarget(transport.getTarget());
	}

}
