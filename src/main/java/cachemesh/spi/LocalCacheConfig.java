package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Serderializer;
import cachemesh.common.HasName;

@lombok.Getter
@lombok.experimental.SuperBuilder
public abstract class LocalCacheConfig<T> implements HasName {

	@lombok.Getter
	private final String name;

	private final Class<T> valueClass;

	@lombok.Getter
	private final Serderializer serder;

	@lombok.Getter
	private final LocalCache.Factory<T> factory;


	public LocalCacheConfig(String name, Class<T> valueClass, Serderializer serder, LocalCache.Factory<T> factory) {
		this.name = name;
		this.valueClass = valueClass;
		this.serder = serder;
		this.factory = factory;
	}

	// public Class<T> getValueClass() {
	// 	return this.valueClass;
	// }

	public abstract LocalCacheConfig<T> buildAnother(String name, Class<T> valueClass);

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put( "valueClass", getValueClass());
		r.put( "serder", getSerder().toMap());
		r.put( "factory", getFactory().toMap());

		return r;
	}

}
