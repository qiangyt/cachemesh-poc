package cachemesh.grpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import cachemesh.MeshNetwork;
import cachemesh.common.err.InternalException;
import cachemesh.side.SideCacheManager;

public class GrpcManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(GrpcManager.class);

	private final Map<String,GrpcService> services = new ConcurrentHashMap<>();

	private final GrpcClientBuilder clientBuilder = new GrpcClientBuilder();

	private final int shutdownSeconds = 60;

	@lombok.Getter
	private volatile boolean inShutdownHook = false;


	public GrpcService createService(GrpcConfig config, SideCacheManager sideCacheManager) {
		var r = new GrpcService(config, sideCacheManager);
		this.services.compute(config.getTarget(), (target, prev) -> {
			if (prev != null) {
				throw new InternalException("duplicated grpc service for %s", target);
			}
			return r;
		});
		return r;
	}

	public GrpcClient buildClient(GrpcConfig config) {
		return this.clientBuilder.build(config);
	}


	public void logShutdownError(Exception err) {
		if (this.inShutdownHook) {
			err.printStackTrace(System.err);
		} else {
			LOGGER.error("shutdown error", err);
		}
	}


	public void logShutdown(String messageFormat, Object... args) {
		String msg = String.format(messageFormat, args);
		if (this.inShutdownHook) {
			System.err.println(msg);
		} else {
			LOGGER.info(msg);
		}
	}

	public void shutdown() {

		logShutdown("grpc service shutdown...");

		var grpcServices = this.services.values();
		var latch = new CountDownLatch(grpcServices.size());

		grpcServices.forEach(grpcService -> {
			new Thread(() -> {
				try {
					grpcService.close();
				} catch (Exception e) {
					logShutdownError(e);
				}
			}).start();
			latch.countDown();
		});

		try {
			latch.await(this.shutdownSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logShutdownError(e);
		}

		logShutdown("grpc service shutdown: done");
	}

	public synchronized void bootstrap() {
		this.services.values().forEach(GrpcService::launch);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				GrpcManager.this.inShutdownHook = true;
				try {
					close();
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			}
		}));
	}

	public void blockUntilTermination(boolean forever) {
		this.services.values().forEach((grpcService) -> {
			try {
				grpcService.awaitTermination(forever);
			} catch (InterruptedException ex) {
				// logShutdown("mesh network shutdown...");
				ex.printStackTrace(System.err);
			}
		});
	}

}
