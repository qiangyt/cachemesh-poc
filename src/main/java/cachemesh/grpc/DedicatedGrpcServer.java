package cachemesh.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cachemesh.common.LifeStage;
import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
public class DedicatedGrpcServer extends AbstractShutdownable implements GrpcServer {

	private final ServerBuilder<?> builder;

	@Setter(AccessLevel.PROTECTED)
	private Server instance;

	private final GrpcConfig config;

	private final LifeStage lifeStage;

	public DedicatedGrpcServer(GrpcConfig config, ShutdownManager shutdownManager) {
		super(config.getTarget(), shutdownManager);

		this.config = config;

		this.builder = Grpc.newServerBuilderForPort(config.getPort(), InsecureServerCredentials.create());
		this.lifeStage = new LifeStage("grpc-server", config.getTarget(), getLogger());
	}

	@Override
	public boolean isStarted() {
		return getLifeStage().getType() == LifeStage.Type.started;
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

	@Override
	public void stop(int timeoutSeconds) throws InterruptedException {
		shutdown(timeoutSeconds);
	}

	public void start(int timeoutSeconds) {
		getLifeStage().starting();

		var inst = getBuilder().build();

		try {
			inst.start();
			setInstance(inst);

			getLifeStage().started();
		} catch (IOException e) {
			throw new InternalException(e, "%s: failed to start server", getName());
		}
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		getLifeStage().stopping();

		var inst = getInstance();
		setInstance(null);

		inst.shutdown();
		inst.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);

		getLifeStage().stopped();
	}

}

