package cachemesh.spi;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.core.GetResult;

@ThreadSafe
public interface NodeCache {

	GetResult<byte[]> getSingle(String cacheName, String key, long version);

	// return version
	long putSingle(String cacheName, String key, byte[] value);

	default boolean isLocal() {
		return false;
	}

	default <T> T getSingleObject(String cacheName, String key) {
		if (isLocal()) {
			throw new UnsupportedOperationException("to be implemented");
		}
		throw new UnsupportedOperationException("unsupported by remote node cache");
	}

	default <T> long putSingleObject(String cacheName, String key, T value, Class<T> valueClass) {
		if (isLocal()) {
			throw new UnsupportedOperationException("to be implemented");
		}
		throw new UnsupportedOperationException("unsupported by remote node cache");
	}


}
