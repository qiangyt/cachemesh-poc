package cachemesh.common;

import java.util.HashMap;
import java.util.Map;

public interface HasName extends Mappable {

	String getName();

	@Override
	default Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();
		r.put("name", getName());
		return r;
	}

	static Map<String, Map<String, Object>> toMaps(Map<String, ? extends HasName> map) {
		var r = new HashMap<String, Map<String, Object>>();
		for (HasName i: map.values()) {
			r.put(i.getName(), i.toMap());
		}
		return r;
	}

}
