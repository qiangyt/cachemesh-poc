package cachemeshpoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@lombok.Getter
@lombok.ToString
public class CacheEntry<T> {

	private final String key;
	private final T value;

	public CacheEntry(String key, T value) {
		this.key = key;
		this.value = value;
	}

	public static <K> Map<String, K> toMap(Iterable<CacheEntry<K>> entries) {
		var r = new HashMap<String, K>();
		entries.forEach(entry -> {
			r.put(entry.getKey(), entry.getValue());
		});
		return r;
	}

	public static <K> List<CacheEntry<K>> fromMap(Map<String, K> entries) {
		var r = new ArrayList<CacheEntry<K>>(entries.size());
		for (var entry: entries.entrySet()) {
			r.add(new CacheEntry<K>(entry.getKey(), entry.getValue()));
		}
		return r;
	}

}
