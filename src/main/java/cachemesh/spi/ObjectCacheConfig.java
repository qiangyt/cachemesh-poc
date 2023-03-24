package cachemesh.spi;

import cachemesh.common.Serderializer;

public class ObjectCacheConfig<T> extends CommonCacheConfig<T> {

	public ObjectCacheConfig(String name, Class<T> valueClass, Serderializer serder, ObjectCacheFactory<T> factory) {
		super(name, valueClass, serder, factory);
	}

}
