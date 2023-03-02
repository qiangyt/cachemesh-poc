package cachemeshpoc.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@lombok.Getter
@lombok.ToString
public class Entry<T> {

	public enum Status {
		NOT_FOUND,
		CHANGED,
		NO_CHANGE;
	}

	@lombok.Getter
	@lombok.ToString
	public static class Head {

		private final String key;
		private final long version;

		public Head(String key, long version) {
			this.key = key;
			this.version = version;
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

		private final long version;

		public Value(T data, long version) {
			this.data = data;
			this.version = version;
		}

	}


	private final String key;

	private final Value<T> value;

	public Entry(String key, Value<T> value) {
		this.key = key;
		this.value = value;
	}

	public Entry(Map.Entry<String, Value<T>> mapEntry) {
		this(mapEntry.getKey(), mapEntry.getValue());;
	}

	public static <T> Map<String, Value<T>> toMap(Iterable<Entry<T>> entries) {
		var r = new HashMap<String, Value<T>>();
		for (var entry: entries) {
			r.put(entry.getKey(), entry.getValue());
		}
		return r;
	}

	public static <T> Collection<Entry<T>> fromMap(Map<String, Value<T>> entryMap) {
		var r = new ArrayList<Entry<T>>(entryMap.size());
		for (var mapEntry: entryMap.entrySet()) {
			r.add(new Entry<T>(mapEntry));
		}
		return r;
	}

}
