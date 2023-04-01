package cachemesh.common.shutdown;

import org.slf4j.Logger;

import cachemesh.common.util.LogHelper;
import lombok.Getter;


@Getter
public abstract class AbstractShutdownable implements ManagedShutdownable {

	private final Logger logger;

	private final String name;

	private final ShutdownManager shutdownManager;

	protected AbstractShutdownable(String name, ShutdownManager shutdownManager) {
		this(name, shutdownManager, 0);
	}

	protected AbstractShutdownable(String name, ShutdownManager shutdownManager, int shutdownTimeoutSeconds) {
		this.name = name;
		this.logger = LogHelper.getLogger(this);

		this.shutdownManager = shutdownManager;
		if (shutdownManager != null) {
			shutdownManager.register(this, shutdownTimeoutSeconds);
		}
	}

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		if (isShutdownNeeded() == false) {
			throw new IllegalStateException(getName() + " doesn't need shutdown");
		}

		var sd = getShutdownManager();
		if (sd != null) {
			sd.shutdown(this, timeoutSeconds);
		} else {
			onShutdown(createShutdownLogger(), timeoutSeconds);
		}
	}

	public ShutdownLogger createShutdownLogger() {
		return new ShutdownLogger(getLogger());
	}

}
