package cachemesh.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import static net.logstash.logback.argument.StructuredArguments.kv;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.util.LogHelper;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

@ThreadSafe
public class GrpcServer implements HasName, AutoCloseable {

	@lombok.Getter
	private final GrpcConfig config;

	private final ServerBuilder<?> instanceBuilder;

	private final Logger logger;

	private Server instance = null;

	private boolean started = false;

	public GrpcServer(GrpcConfig config) {
		this.config = config;
		this.instanceBuilder = Grpc.newServerBuilderForPort(config.getPort(), InsecureServerCredentials.create());
		this.logger = LogHelper.getLogger(this);
	}

	public synchronized void addService(BindableService service) {
		if (isStarted()) {
			throw new InternalException("%s: cannot add service any more once server started", getName());
		}
		this.instanceBuilder.addService(service);
	}

	public synchronized boolean isTerminated() {
		return this.instance != null && this.instance.isTerminated();
	}

	public synchronized boolean isStarted() {
		return this.started;
	}

	@Override
	public String toString() {
		return this.config.toString() + "." + (isStarted() ? "started" : "notStarted");
	}

	public synchronized void start() {
		if (isStarted()) {
			throw new InternalException("%s: server already started", getName());
		}

		var nameKv = kv("name", getName());
		this.logger.info("{}: starting ...", nameKv, LogHelper.kv("config", getConfig()));

		var inst = this.instanceBuilder.build();

		try {
			inst.start();

			this.instance = inst;
			this.started = true;

			this.logger.info("{}: start done", nameKv);
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
	public synchronized void close() throws Exception {
		ensureStarted();

		var nameKv = kv("name", getName());
		this.logger.info("{}: shutdowning ..., {}", nameKv, kv("timeout", this.config.getServiceShutdownSeconds() + "s"));

		this.instance.shutdown();
		blockUntilTermination(0);

		this.started = false;

		this.logger.info("{}: shutdown done", nameKv);
	}

	public void blockUntilTermination(int shutdownSeconds) throws InterruptedException {
		ensureStarted();

		if (shutdownSeconds <= 0) {
			shutdownSeconds = this.config.getServiceShutdownSeconds();
		} else {
			shutdownSeconds = Math.min(shutdownSeconds, this.config.getServiceShutdownSeconds());
		}

		this.instance.awaitTermination(shutdownSeconds, TimeUnit.SECONDS);
	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

}

