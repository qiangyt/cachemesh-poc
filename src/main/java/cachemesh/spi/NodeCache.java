package cachemesh.spi;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.common.HasName;
import cachemesh.spi.base.GetResult;

@ThreadSafe
public interface NodeCache extends HasName, AutoCloseable {

	GetResult<byte[]> getSingle(String cacheName, String key, long version);

	// return version
	long putSingle(String cacheName, String key, byte[] value);


}
