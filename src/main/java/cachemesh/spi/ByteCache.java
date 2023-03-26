package cachemesh.spi;

import cachemesh.spi.base.Value;

public interface ByteCache
	extends LocalCache<byte[], Value<byte[]>, ByteCacheConfig> {


}
