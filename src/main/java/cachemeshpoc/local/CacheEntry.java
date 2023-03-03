package cachemeshpoc.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@lombok.Getter
@lombok.ToString
public class CacheEntry<T> {

	@lombok.Getter
	@lombok.ToString
	public static class Head {

		private final String key;
		private final long versh;

		public Head(String key, long versh) {
			this.key = key;
			this.versh = versh;
		}

		public static Collection<String> extractKeys(Collection<Head> heads) {
			var r = new ArrayList<String>(heads.size());
			for (var head : heads) {
				r.add(head.getKey());
			}
			return r;
		}
	}

	@lombok.Getter
	@lombok.ToString
	public static class Value<T> {

		private final T data;

		private final long versh;

		public Value(T data, long versh) {
			this.data = data;
			this.versh = versh;
		}

	}


	private final String key;

	private final Value<T> value;

	public CacheEntry(String key, Value<T> value) {
		this.key = key;
		this.value = value;
	}

	public CacheEntry(Map.Entry<String, Value<T>> mapEntry) {
		this(mapEntry.getKey(), mapEntry.getValue());;
	}

	public static <T> Map<String, Value<T>> toMap(Iterable<CacheEntry<T>> entries) {
		var r = new HashMap<String, Value<T>>();
		for (var entry: entries) {
			r.put(entry.getKey(), entry.getValue());
		}
		return r;
	}

	public static <T> Collection<CacheEntry<T>> fromMap(Map<String, Value<T>> entryMap) {
		var r = new ArrayList<CacheEntry<T>>(entryMap.size());
		for (var mapEntry: entryMap.entrySet()) {
			r.add(new CacheEntry<T>(mapEntry));
		}
		return r;
	}

}
