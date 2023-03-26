package cachemesh.grpc;

import java.util.concurrent.TimeUnit;

import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import io.grpc.ManagedChannel;
import lombok.Getter;


@Getter
public class GrpcClient extends AbstractShutdownable {

	private final GrpcConfig config;

	private ManagedChannel channel = null;

	public GrpcClient(GrpcConfig config) {
		super(config.getName());

		this.config = config;
		this.channel = config.createClientChannel();

		setShutdownNeeded(true);
	}

	@Override
	public String toString() {
		return getConfig().toString();
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		var ch = getChannel();
		ch.shutdown();

		shutdownLogger.info("await termination");
		ch.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
	}

}

