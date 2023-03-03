package cachemeshpoc.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LocalCache<T> extends AutoCloseable {

	public static interface Factory {
		<T> LocalCache<T> create(String cacheName, Class<T> valueClass);
	}


	@lombok.Getter
	@lombok.ToString
	public class Entry<K> {

		private final String key;
		private final K value;

		public Entry(String key, K value) {
			this.key = key;
			this.value = value;
		}

		public static <R> Map<String, R> toMap(Iterable<Entry<R>> entries) {
			var r = new HashMap<String, R>();
			entries.forEach(entry -> {
				r.put(entry.getKey(), entry.getValue());
			});
			return r;
		}

		public static <R> List<Entry<R>> fromMap(Map<String, R> entries) {
			var r = new ArrayList<Entry<R>>(entries.size());
			for (var entry: entries.entrySet()) {
				r.add(new Entry<R>(entry.getKey(), entry.getValue()));
			}
			return r;
		}

	}


	String getName();

	Class<T> getValueClass();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	T getSingle(String key);

	Map<String, T> getMultiple(Collection<String> keys);

	void putSingle(String key, T value);

	void putMultiple(Collection<Entry<T>> entries);

	Collection<String> getAllKeys();

}
