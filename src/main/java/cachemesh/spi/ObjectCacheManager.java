package cachemesh.spi;

import cachemesh.common.HasName;

public class ObjectCacheManager<T>
	extends CommonCacheManager<T, ObjectCache<T>, ObjectCacheConfig<T>>
	implements HasName {

	public ObjectCacheManager(ObjectCacheConfig<T> defaultConfig) {
		super(defaultConfig);
	}

	@Override
	public String getName() {
		return "object";
	}


}
