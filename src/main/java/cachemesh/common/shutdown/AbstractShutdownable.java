package cachemesh.common.shutdown;

import org.slf4j.Logger;

import cachemesh.common.util.LogHelper;
import lombok.Getter;


@Getter
public abstract class AbstractShutdownable implements Shutdownable {

	protected final Logger logger;

	private final String name;

	private ShutdownSupport shutdownSupport;

	private boolean shutdownNeeded;

	public AbstractShutdownable(String name) {
		this.name = name;
		this.logger = LogHelper.getLogger(this);
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
			onShutdown(shutdownLogger(), timeoutSeconds);
		}
	}

	protected void setShutdownNeeded(boolean shutdownNeeded) {
		this.shutdownNeeded = shutdownNeeded;
	}

	@Override
	public void onShutdownRegistered(ShutdownSupport shutdownSupport) {
		this.shutdownSupport = shutdownSupport;
	}

	@Override
	public ShutdownLogger shutdownLogger() {
		return new ShutdownLogger(getLogger());
	}

}
