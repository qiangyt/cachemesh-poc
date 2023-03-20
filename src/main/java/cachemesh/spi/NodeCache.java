package cachemesh.spi;

import cachemesh.common.HasName;
import cachemesh.spi.base.GetResult;

public interface NodeCache extends HasName {

	GetResult<byte[]> getSingle(String key, long version);

	// return version
	long putSingle(String key, byte[] bytes);


}
