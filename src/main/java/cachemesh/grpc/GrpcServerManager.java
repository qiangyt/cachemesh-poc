package cachemesh.grpc;

import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.shutdown.ShutdownableResourceManager;

public class GrpcServerManager extends ShutdownableResourceManager<GrpcServer, GrpcConfig> {

	public static final GrpcServerManager DEFAULT = new GrpcServerManager("default-grpc-server-manager", ShutdownSupport.DEFAULT);

	public GrpcServerManager(String name, ShutdownSupport shutdownSupport) {
		super(name, shutdownSupport, 0);
	}

	public GrpcServerManager(String name, ShutdownSupport shutdownSupport, int shutdownTimeoutSeconds) {
		super(name, shutdownSupport, shutdownTimeoutSeconds);
	}

	@Override
	protected GrpcServer create(GrpcConfig config) {
		return new GrpcServer(config, getShutdownSupport(), this);
	}


	public void startAll() {
		getResources().values().forEach(GrpcServer::start);
	}

}
