package cachemesh.grpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import cachemesh.common.err.InternalException;

@ThreadSafe
public class GrpcServerManager implements AutoCloseable {

	public static final GrpcServerManager DEFAULT = new GrpcServerManager();

	private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerManager.class);

	private final Map<String, GrpcServer> servers = new HashMap<>();

	private final int shutdownSeconds = 60;

	@lombok.Getter
	private volatile boolean inShutdownHook = false;

	public synchronized GrpcServer resolve(GrpcConfig config) {
		return this.servers.compute(config.getTarget(), (target, prev) -> {
			if (prev != null) {
				throw new InternalException("duplicated grpc server for %s", target);
			}
			return new GrpcServer(config);
		});
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

	@Override
	public synchronized void close() throws Exception {

		logShutdown("shutdown grpc servers ...");

		var latch = new CountDownLatch(this.servers.size());

		this.servers.values().forEach(server -> {
			new Thread(() -> {
				try {
					server.close();
				} catch (Exception e) {
					logShutdownError(e);
				}
			}).start();
			latch.countDown();
		});

		latch.await(this.shutdownSeconds, TimeUnit.SECONDS);

		logShutdown("shutdown grpc servers: done");
	}

	public synchronized void startAll() {
		this.servers.values().forEach(GrpcServer::start);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				GrpcServerManager.this.inShutdownHook = true;
				try {
					close();
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			}
		}));
	}

	public synchronized boolean blockUntilTermination() throws InterruptedException {
		var latch = new CountDownLatch(this.servers.size());

		this.servers.values().forEach(server -> {
			new Thread(() -> {
				try {
					server.blockUntilTermination(this.shutdownSeconds - 1);
				} catch (InterruptedException ex) {
					// logShutdown("mesh network shutdown...");
					ex.printStackTrace(System.err);
				}

				latch.countDown();
			});
		});

		return latch.await(this.shutdownSeconds, TimeUnit.SECONDS);
	}

}
