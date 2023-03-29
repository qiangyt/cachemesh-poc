package cachemesh.spi;

import cachemesh.common.HasName;
import cachemesh.spi.base.Value;

public interface ByteCacheFactory
	extends LocalCacheFactory<byte[], Value<byte[]>, ByteCacheConfig, ByteCache> {

}
