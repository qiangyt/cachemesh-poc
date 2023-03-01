package qiangyt.cachemeshpoc.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@lombok.Data
@lombok.Builder
public class Entry {

	private final String key;

	private final EntryValue value;

	public static Map<String, EntryValue> toMap(Iterable<Entry> entries) {
		var r = new HashMap<String, EntryValue>();
		for (var entry: entries) {
			r.put(entry.getKey(), entry.getValue());
		}
		return r;
	}

	public static Collection<Entry> fromMap(Map<String, EntryValue> entryMap) {
		var r = new ArrayList<Entry>(entryMap.size());
		for (var entry: entryMap.entrySet()) {
			r.add(fromMapEntry(entry));
		}
		return r;
	}

	public static Entry fromMapEntry(Map.Entry<String, EntryValue> mapEntry) {
		return builder()
				.key(mapEntry.getKey())
				.value(mapEntry.getValue())
				.build();
	}


}
