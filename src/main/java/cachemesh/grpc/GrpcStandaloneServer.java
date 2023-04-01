package cachemesh.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.util.LogHelper;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
public class GrpcStandaloneServer extends AbstractShutdownable implements GrpcServer {

	private final ServerBuilder<?> builder;

	@Setter(AccessLevel.PROTECTED)
	private Server instance;

	private final GrpcConfig config;

	@Setter(AccessLevel.PROTECTED)
	private volatile boolean started;

	public GrpcStandaloneServer(GrpcConfig config, ShutdownSupport shutdownSupport) {
		super(config.getTarget(), shutdownSupport);

		this.config = config;

		this.builder = Grpc.newServerBuilderForPort(config.getPort(), InsecureServerCredentials.create());
	}

	@Override
	public boolean isShutdownNeeded() {
		return isStarted();
	}

	@Override
	public void addService(BindableService service) {
		if (isStarted()) {
			throw new InternalException("%s: cannot add service any more once server started", getName());
		}
		getBuilder().addService(service);
	}

	public void start() {
		if (isStarted()) {
			throw new InternalException("%s: server already started", getName());
		}

		getLogger().info("starting ...", LogHelper.kv("config", getConfig()));

		var inst = getBuilder().build();

		try {
			inst.start();
			setInstance(inst);

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

		var inst = getInstance();
		inst.shutdown();
		blockUntilTermination(0);

		setInstance(null);
	}

	public void blockUntilTermination(int timeoutSeconds) throws InterruptedException {
		ensureStarted();

		getInstance().awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
	}

}

