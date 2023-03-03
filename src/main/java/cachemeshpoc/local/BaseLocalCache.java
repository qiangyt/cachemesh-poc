package cachemeshpoc.local;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cachemeshpoc.CacheEntry;

@lombok.Getter
@lombok.ToString
public abstract class BaseLocalCache<T> implements LocalCache<T> {

	private final String name;

	private final Class<T> valueClass;

	protected BaseLocalCache(String name, Class<T> valueClass) {
		this.name = name;
		this.valueClass = valueClass;
	}

	@Override
	public void invalidateMultiple(Collection<String> keys) {
		keys.forEach(this::invalidateSingle);
	}


	@Override
	public Map<String, T> getMultiple(Collection<String> keys) {
		var r = new HashMap<String, T>();
		keys.forEach(key -> {
			var v = this.getSingle(key);
			if (v != null) {
				r.put(key, v);
			}
		});
		return r;
	}

	@Override
	public void putMultiple(Collection<CacheEntry<T>> entries) {
		entries.forEach(entry -> putSingle(entry.getKey(), entry.getValue()));
	}

}
