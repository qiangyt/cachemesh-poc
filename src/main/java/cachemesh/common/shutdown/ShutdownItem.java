package cachemesh.common.shutdown;
import lombok.Getter;

@Getter
public class ShutdownItem {

	private final Shutdownable target;

	private final int timeoutSeconds;

	private volatile boolean shutdowned;

	private final ShutdownSupport support;

	private final ShutdownLogger logger;


	public ShutdownItem(ShutdownSupport support, Shutdownable target, int timeoutSeconds) {
		this.target = target;
		this.timeoutSeconds = timeoutSeconds;
		this.shutdowned = false;
		this.support = support;

		if (target instanceof AbstractShutdownable) {
			this.logger = ((AbstractShutdownable)target).createShutdownLogger();
		} else {
			this.logger = new ShutdownLogger(target.getLogger());
		}
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
			this.logger.error(e, "got error during shutdown");
		}

		this.shutdowned = true;
		this.logger.info("end to shutdown");
	}


}
