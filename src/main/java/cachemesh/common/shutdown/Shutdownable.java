package cachemesh.common.shutdown;

public interface Shutdownable {

	void shutdown(int timeoutSeconds) throws InterruptedException;

}
