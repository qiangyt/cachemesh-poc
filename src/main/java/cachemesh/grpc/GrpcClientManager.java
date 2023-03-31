package cachemesh.grpc;

import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.shutdown.ShutdownableManager;

public class GrpcClientManager extends ShutdownableManager<GrpcClient, GrpcConfig> {

	public static final GrpcClientManager DEFAULT = new GrpcClientManager("default-grpc-client-manager", ShutdownSupport.DEFAULT);

	public GrpcClientManager(String name, ShutdownSupport shutdownSupport) {
		super(name, shutdownSupport, 0);
	}

	public GrpcClientManager(String name, ShutdownSupport shutdownSupport, int shutdownTimeoutSeconds) {
		super(name, shutdownSupport, shutdownTimeoutSeconds);
	}

	@Override
	protected GrpcClient create(GrpcConfig config) {
		return new GrpcClient(config, getShutdownSupport(), this);
	}

}
