package cachemesh.grpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.util.LogHelper;

@ThreadSafe
public class GrpcClientManager {

	public static final GrpcClientManager DEFAULT = new GrpcClientManager(ShutdownSupport.DEFAULT);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, GrpcClient> clients = new ConcurrentHashMap<>();

	private ShutdownSupport shutdown;

	public GrpcClientManager(ShutdownSupport shutdown) {
		this.shutdown = shutdown;
	}

	public GrpcClient resolve(GrpcConfig config) {
		return this.clients.computeIfAbsent(config.getTarget(), target -> {
			var r = new GrpcClient(config);
			if (this.shutdown != null) {
				this.shutdown.register(r);
			}

			this.logger.info("created grpc client: {}", LogHelper.entries(r));
			return r;
		});
	}


}
