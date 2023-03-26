package cachemesh.common.shutdown;

import cachemesh.common.HasName;

public interface Shutdownable extends HasName {

	void shutdown(int timeoutSeconds) throws InterruptedException;

	void onShutdownRegistered(ShutdownSupport shutdownSupport);

	void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException;

	ShutdownLogger shutdownLogger();

	boolean isShutdownNeeded();

}
