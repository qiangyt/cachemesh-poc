package cachemesh.spi;

import cachemesh.common.HasName;

public interface ByteCacheFactory extends HasName {

	ByteCache create(ByteCacheConfig config);

}
