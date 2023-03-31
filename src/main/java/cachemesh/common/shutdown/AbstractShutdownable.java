package cachemesh.common.shutdown;

import org.slf4j.Logger;

import cachemesh.common.util.LogHelper;
import lombok.Getter;


@Getter
public abstract class AbstractShutdownable implements Shutdownable {

	@Getter
	private final Logger logger;

	private final String name;

	private ShutdownSupport shutdownSupport;

	protected AbstractShutdownable(String name, ShutdownSupport shutdownSupport) {
		this(name, shutdownSupport, 0);
	}

	protected AbstractShutdownable(String name, ShutdownSupport shutdownSupport, int shutdownTimeoutSeconds) {
		this.name = name;
		this.logger = LogHelper.getLogger(this);

		this.shutdownSupport = shutdownSupport;
		if (shutdownSupport != null) {
			shutdownSupport.register(this, shutdownTimeoutSeconds);
		}
	}

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		if (isShutdownNeeded() == false) {
			throw new IllegalStateException(getName() + " doesn't need shutdown");
		}

		var sd = getShutdownSupport();
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
