package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;
import cachemesh.common.Serderializer;

@lombok.Getter
@lombok.experimental.SuperBuilder
public class CommonCacheConfig<T> implements HasName {

	private final String name;

	private final Class<T> valueClass;

	private final Serderializer serder;

	@lombok.Getter
	private final CommonCacheFactory factory;

	public CommonCacheConfig(String name, Class<T> valueClass, Serderializer serder, CommonCacheFactory factory) {
		this.name = name;
		this.valueClass = valueClass;
		this.serder = serder;
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	public <T2, C2 extends CommonCacheConfig<T2>> C2 buildAnother(String name, Class<T2> valueClass) {
		return (C2)new CommonCacheConfig<T2>(name, valueClass, getSerder(), getFactory());
	}

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
