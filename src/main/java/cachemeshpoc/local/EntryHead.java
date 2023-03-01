package cachemeshpoc.local;

import java.util.ArrayList;
import java.util.Collection;

@lombok.Data
@lombok.Builder
public class EntryHead {

	String key;
	long version;

	public static Collection<String> keys(Collection<EntryHead> heads) {
		var r = new ArrayList<String>(heads.size());
		for (var head : heads) {
			r.add(head.getKey());
		}
		return r;
	}
}
