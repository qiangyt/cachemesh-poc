package cachemesh.common.shutdown;

import org.slf4j.Logger;

import cachemesh.common.HasName;

public interface Shutdownable extends HasName {

	void shutdown(int timeoutSeconds) throws InterruptedException;

	void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException;

	default boolean isShutdownNeeded() {
		return true;
	}

	Logger getLogger();

}
