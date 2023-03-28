package cachemesh.spi;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.common.HasName;
import cachemesh.spi.base.GetResult;

@ThreadSafe
public interface NodeCache extends HasName {

	NodeCacheConfig getConfig();

	@Override
	default String getName() {
		return getConfig().getName();
	}

	GetResult<byte[]> getSingle(String key, long version);

	// return version
	long putSingle(String key, byte[] value);


}
