package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;
import cachemesh.common.Serderializer;

@lombok.Getter
@lombok.experimental.SuperBuilder
public class LocalCacheConfig<T> implements HasName {

	private final String name;

	private final Class<T> valueClass;

	private final Serderializer serder;

	private final boolean cacheBytes;

	@lombok.Getter
	private final LocalCacheFactory factory;

	public LocalCacheConfig(String name,
	 						 Class<T> valueClass,
							 Serderializer serder,
							 boolean cacheBytes,
							 LocalCacheFactory factory) {
		this.name = name;
		this.valueClass = valueClass;
		this.serder = serder;
		this.cacheBytes = cacheBytes;
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	public <T2, C2 extends LocalCacheConfig<T2>> C2 buildAnother(String name, Class<T2> valueClass) {
		return (C2)new LocalCacheConfig<T2>(name, valueClass, getSerder(), isCacheBytes(), getFactory());
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put( "valueClass", getValueClass());
		r.put( "serder", getSerder().toMap());
		r.put( "cacheBytes", isCacheBytes());
		r.put( "factory", getFactory().toMap());

		return r;
	}

}
