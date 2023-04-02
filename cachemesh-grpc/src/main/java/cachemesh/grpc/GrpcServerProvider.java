package cachemesh.grpc;

import cachemesh.common.Manager;
import cachemesh.common.shutdown.ShutdownManager;
import lombok.Getter;

@Getter
public class GrpcServerProvider extends Manager<GrpcConfig, GrpcServer> {

	public static final GrpcServerProvider DEFAULT = new GrpcServerProvider(ShutdownManager.DEFAULT);

	private final ShutdownManager shutdownManager;

	public GrpcServerProvider(ShutdownManager shutdownManager) {
		this.shutdownManager = shutdownManager;
	}

	@Override
	protected String retrieveKey(GrpcConfig config) {
		return config.getTarget();
	}

	@Override
	protected GrpcServer doCreate(GrpcConfig config) {
		return new DedicatedGrpcServer(config, getShutdownManager());
	}

	@Override
	protected void doRelease(GrpcConfig config, GrpcServer server, int timeoutSeconds)
			throws InterruptedException {
		server.stop(timeoutSeconds);
	}

}
