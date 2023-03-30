package cachemesh.spi;

import cachemesh.common.HasName;

public interface LocalCacheFactory extends HasName {

	LocalCache create(LocalCacheConfig config);

}
