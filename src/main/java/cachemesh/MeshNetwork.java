package cachemesh;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.shutdown.Shutdownable;
import cachemesh.common.util.LogHelper;
import cachemesh.core.LocalCacheManager;
import cachemesh.core.MeshCacheManager;
import cachemesh.core.MeshNodeManager;
import cachemesh.core.TransportRegistry;
import lombok.Getter;

@Getter
public class MeshNetwork implements Shutdownable, HasName {

	private final MeshNetworkConfig config;

	private final LocalCacheManager nearCacheManager;

	private final LocalCacheManager localCacheManager;

	private boolean bootstrapped;

	private final Logger logger;

	private final MeshCacheManager meshCacheManager;

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

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		if (this.bootstrapped) {
			throw new InternalException("not bootstrapped");
		}

		this.logger.info("mesh network shutdown...");

		// TODO

		this.bootstrapped = false;
		this.logger.info("mesh network shutdown: done");
	}

}
