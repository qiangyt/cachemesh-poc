package cachemesh.common.shutdown;

import org.slf4j.Logger;

import cachemesh.common.HasName;

public interface ManagedShutdownable extends Shutdownable, HasName {

	void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException;

	default boolean isShutdownNeeded() {
		return true;
	}

	Logger getLogger();

}
