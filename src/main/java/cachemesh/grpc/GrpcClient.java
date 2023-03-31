package cachemesh.grpc;

import java.util.concurrent.TimeUnit;

import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.shutdown.ShutdownableResource;
import io.grpc.ManagedChannel;
import lombok.Getter;


@Getter
public class GrpcClient extends ShutdownableResource<GrpcConfig> {

	private ManagedChannel channel = null;


	public GrpcClient(GrpcConfig config, ShutdownSupport shutdownSupport, GrpcClientManager clientManager) {
		super(config, shutdownSupport, clientManager);
		this.channel = config.createClientChannel();
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		var ch = getChannel();
		ch.shutdown();

		shutdownLogger.info("await termination");
		ch.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
	}

}

