package cachemesh.spi.base;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@lombok.Getter
@lombok.ToString
public class LocalCacheEntry<T> {

	private final String key;
	private final Value<T> value;

	public LocalCacheEntry(String key, Value<T> value) {
			this.key = key;
			this.value = value;
		}

	public static <R> Map<String, Value<R>> toMap(Iterable<LocalCacheEntry<R>> entries) {
		var r = new HashMap<String, Value<R>>();
		entries.forEach(entry -> {
			r.put(entry.getKey(), entry.getValue());
		});
		return r;
	}

	public static <R> List<LocalCacheEntry<R>> fromMap(Map<String, Value<R>> entries) {
		var r = new ArrayList<LocalCacheEntry<R>>(entries.size());
		for (var entry : entries.entrySet()) {
			r.add(new LocalCacheEntry<R>(entry.getKey(), entry.getValue()));
		}
		return r;
	}

}
