package cachemesh.common.shutdown;

@lombok.Getter
public class ShutdownItem {

	final private Shutdownable target;

	final private int timeoutSeconds;

	private volatile boolean shutdowned;

	private final ShutdownSupport support;

	private final ShutdownLogger logger;


	public ShutdownItem(ShutdownSupport support, Shutdownable target, int timeoutSeconds) {
		this.target = target;
		this.timeoutSeconds = timeoutSeconds;
		this.shutdowned = false;
		this.support = support;
		this.logger = target.shutdownLogger();
	}


	public boolean isShutdownNeeded() {
		if (this.target.isShutdownNeeded() == false) {
			return false;
		}
		return isShutdowned() == false;
	}

	public void shutdown(int timeoutSeconds) {
		this.logger.info("begin to shutdown");

		if (timeoutSeconds <= 0) {
			timeoutSeconds = getTimeoutSeconds();
		}

		try {
			this.target.onShutdown(this.logger, timeoutSeconds);
		} catch (Exception e) {
			this.target.shutdownLogger().error(e, "got error during shutdown");
		}

		this.shutdowned = true;
		this.logger.info("end to shutdown");
	}


}
