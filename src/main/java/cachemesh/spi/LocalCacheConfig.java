package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;
import cachemesh.common.Serderializer;

@lombok.Getter
@lombok.experimental.SuperBuilder
public class LocalCacheConfig implements HasName {

	private final String name;

	private final Class<?> valueClass;

	private final Serderializer serder;

	//private final boolean cacheBytes;

	@lombok.Getter
	private final LocalCacheFactory factory;

	public LocalCacheConfig(String name,
	 						 Class<?> valueClass,
							 Serderializer serder,
	//						 boolean cacheBytes,
							 LocalCacheFactory factory) {
		this.name = name;
		this.valueClass = valueClass;
		this.serder = serder;
	//	this.cacheBytes = cacheBytes;
		this.factory = factory;
	}

	public LocalCacheConfig buildAnother(String name, Class<?> valueClass) {
		return new LocalCacheConfig(name, valueClass, getSerder(), /*isCacheBytes(), */getFactory());
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put( "valueClass", getValueClass());
		r.put( "serder", getSerder().toMap());
		//r.put( "cacheBytes", isCacheBytes());
		r.put( "factory", getFactory().toMap());

		return r;
	}

}
