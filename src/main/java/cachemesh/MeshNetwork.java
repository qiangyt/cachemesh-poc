package cachemesh;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.util.LogHelper;
import cachemesh.core.MeshCacheManager;
import cachemesh.core.MeshNodeManager;
import cachemesh.core.TransportRegistry;
import cachemesh.core.local.LocalCacheManager;
import cachemesh.grpc.GrpcClientManager;
import cachemesh.grpc.GrpcServerManager;
import cachemesh.lettuce.LettuceChannelManager;
import lombok.Getter;

public class MeshNetwork implements HasName {

	@Getter
	private final MeshNetworkConfig config;

	@Getter
	private final LocalCacheManager nearCacheManager;

	@Getter
	private final LocalCacheManager localCacheManager;

	@Getter
	private boolean bootstrapped;

	private final Logger logger;

	@Getter
	private final MeshCacheManager meshCacheManager;

	@Getter
	private final MeshNodeManager nodeManager;

	public MeshNetwork(MeshNetworkConfig config,
						LocalCacheManager nearCacheManager,
						LocalCacheManager localCacheManager,
						TransportRegistry transportRegistry) {
		this.config = config;
		this.nearCacheManager = nearCacheManager;
		this.localCacheManager = localCacheManager;

		this.bootstrapped = false;
		this.logger = LogHelper.getLogger(this);
		this.nodeManager = new MeshNodeManager(config.getHashing(),
											localCacheManager,
											transportRegistry);
		this.meshCacheManager = new MeshCacheManager(nearCacheManager, this.nodeManager);
	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

	public void bootstrap() {
		if (this.bootstrapped) {
			throw new InternalException("already bootstrapped");
		}

		this.logger.info("mesh network bootstrap: ...");

		getGrpcServerManager().startAll();

		this.bootstrapped = true;
		this.logger.info("mesh network bootstrap: done");
	}

	public void shutdown() throws InterruptedException {
		if (this.bootstrapped) {
			throw new InternalException("not bootstrapped");
		}

		this.logger.info("mesh network shutdown...");

		// TODO

		this.bootstrapped = false;
		this.logger.info("mesh network shutdown: done");
	}

}
