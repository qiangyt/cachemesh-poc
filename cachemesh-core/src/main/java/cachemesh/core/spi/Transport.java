package cachemesh.core.spi;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.core.GetResult;

@ThreadSafe
public interface Transport {

	void start(int timeoutSeconds) throws InterruptedException;

	void stop(int timeoutSeconds) throws InterruptedException;

	GetResult<byte[]> getSingle(String cacheName, String key, long version);

	// return version
	long putSingle(String cacheName, String key, byte[] value);

	boolean isRemote();

	default <T> T getSingleObject(String cacheName, String key) {
		if (isRemote()) {
			throw new UnsupportedOperationException("unsupported by remote transport");
		}
		throw new UnsupportedOperationException("to be implemented");
	}

	default <T> long putSingleObject(String cacheName, String key, T value, Class<T> valueClass) {
		if (isRemote()) {
			throw new UnsupportedOperationException("unsupported by remote transport");
		}
		throw new UnsupportedOperationException("to be implemented");
	}


}
