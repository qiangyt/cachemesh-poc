package cachemesh.common.shutdown;

public abstract class ShutdownableResource<C extends ShutdownableConfig> extends AbstractShutdownable{

	@lombok.Getter
	public final ShutdownableManager<? extends ShutdownableResource<C>,C> resourceManager;

	@lombok.Getter
	public final C config;

	protected ShutdownableResource(C config, ShutdownSupport shutdownSupport, ShutdownableManager<? extends ShutdownableResource<C>,C> resourceManager) {
		super(config.getName(), shutdownSupport, config.getShutdownTimeoutSeconds());
		this.resourceManager = resourceManager;
		this.config = config;
	}

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		if (isShutdownNeeded() == false) {
			throw new IllegalStateException(getName() + " doesn't need shutdown");
		}

		var rm = getResourceManager();
		if (rm != null) {
			rm.remove(this);
		}

		super.shutdown(timeoutSeconds);
	}

}
