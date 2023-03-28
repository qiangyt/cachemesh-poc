package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;

@lombok.Getter
@lombok.experimental.SuperBuilder
public class ByteCacheConfig  implements HasName {

	private final String name;

	@lombok.Getter
	private final ByteCacheFactory factory;

	public ByteCacheConfig(String name, ByteCacheFactory factory) {
		this.name = name;
		this.factory = factory;
	}

	public ByteCacheConfig buildAnother(String name) {
		return new ByteCacheConfig(name, getFactory());
	}

	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("name", getName());
		r.put( "factory", getFactory().toMap());

		return r;
	}

}
