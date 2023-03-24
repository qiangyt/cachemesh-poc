package cachemesh.spi;

import cachemesh.common.HasName;

public interface CommonCacheFactory<T, C extends CommonCacheConfig<T>, K extends CommonCache<T, C>> extends HasName {

		K create(C config);

}
