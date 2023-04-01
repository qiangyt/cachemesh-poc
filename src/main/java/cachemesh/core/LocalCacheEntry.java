package cachemesh.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cachemesh.spi.Value;

import java.util.HashMap;
import lombok.Getter;

@Getter
public class LocalCacheEntry {

	private final String key;
	private final Value value;

	public LocalCacheEntry(String key, Value value) {
			this.key = key;
			this.value = value;
		}

	public static Map<String, Value> toMap(Iterable<LocalCacheEntry> entries) {
		var r = new HashMap<String, Value>();
		entries.forEach(entry -> {
			r.put(entry.getKey(), entry.getValue());
		});
		return r;
	}

	public static List<LocalCacheEntry> fromMap(Map<String, Value> entries) {
		var r = new ArrayList<LocalCacheEntry>(entries.size());
		for (var entry : entries.entrySet()) {
			r.add(new LocalCacheEntry(entry.getKey(), entry.getValue()));
		}
		return r;
	}

}
