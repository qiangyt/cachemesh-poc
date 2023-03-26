package cachemesh.grpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.util.LogHelper;

@ThreadSafe
public class GrpcServerManager {

	public static final GrpcServerManager DEFAULT = new GrpcServerManager(ShutdownSupport.DEFAULT);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, GrpcServer> servers = new ConcurrentHashMap<>();

	private ShutdownSupport shutdown;

	public GrpcServerManager(ShutdownSupport shutdown) {
		this.shutdown = shutdown;
	}

	public GrpcServer create(GrpcConfig config) {
		return this.servers.compute(config.getTarget(), (target, prev) -> {
			if (prev != null) {
				throw new InternalException("duplicated grpc server for %s", target);
			}

			var r = new GrpcServer(config);
			if (this.shutdown != null) {
				this.shutdown.register(r);
			}

			this.logger.info("created grpc server: {}", LogHelper.entries(r));
			return r;
		});
	}

	public void startAll() {
		this.servers.values().forEach(GrpcServer::start);
	}


}
