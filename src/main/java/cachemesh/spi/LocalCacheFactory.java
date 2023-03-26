package cachemesh.spi;

import cachemesh.common.HasName;
import cachemesh.spi.base.Value;

public interface LocalCacheFactory
	<T, V extends Value<T>, C extends LocalCacheConfig<T>, K extends LocalCache<T, V, C>>
	extends HasName {

	K create(C config);

}
