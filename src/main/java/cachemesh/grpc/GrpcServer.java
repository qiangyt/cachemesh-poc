package cachemesh.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.shutdown.ShutdownableResource;
import cachemesh.common.util.LogHelper;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer extends ShutdownableResource<GrpcConfig> {

	private final ServerBuilder<?> builder;

	private volatile Server instance = null;

	public GrpcServer(GrpcConfig config, ShutdownSupport shutdownSupport, GrpcServerManager serverManager) {
		super(config, shutdownSupport, serverManager);
		this.builder = Grpc.newServerBuilderForPort(config.getPort(), InsecureServerCredentials.create());
	}

	@Override
	public boolean isShutdownNeeded() {
		return isStarted();
	}

	public boolean isStarted() {
		return this.instance != null;
	}

	public void addService(BindableService service) {
		if (isStarted()) {
			throw new InternalException("%s: cannot add service any more once server started", getName());
		}
		this.builder.addService(service);
	}

	public void start() {
		if (isStarted()) {
			throw new InternalException("%s: server already started", getName());
		}

		getLogger().info("starting ...", LogHelper.kv("config", getConfig()));

		var inst = this.builder.build();

		try {
			inst.start();
			this.instance = inst;

			getLogger().info("started");
		} catch (IOException e) {
			throw new InternalException(e, "%s: failed to start server", getName());
		}
	}

	public void ensureStarted() {
		if (!isStarted()) {
			throw new InternalException("%s: server not yet started", getName());
		}
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		ensureStarted();

		this.instance.shutdown();
		blockUntilTermination(0);
		this.instance = null;
	}

	public void blockUntilTermination(int timeoutSeconds) throws InterruptedException {
		ensureStarted();
		this.instance.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
	}

}

