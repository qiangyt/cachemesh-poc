package cachemesh.common.shutdown;

import cachemesh.common.HasName;

public interface Shutdownable extends HasName {

	void shutdown(int timeoutSeconds) throws InterruptedException;

}
