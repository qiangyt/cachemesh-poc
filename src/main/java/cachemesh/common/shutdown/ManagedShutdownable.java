package cachemesh.common.shutdown;

import org.slf4j.Logger;

public interface ManagedShutdownable extends Shutdownable {

	void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException;

	default boolean isShutdownNeeded() {
		return true;
	}

	Logger getLogger();

}
