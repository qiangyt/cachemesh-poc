package qiangyt.cachemeshpoc.local;

import java.util.Collection;
import java.util.Map;

public interface LocalCache extends AutoCloseable {

	LocalCacheConfig getConfig();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	ResolveResult resolveSingle(EntryHead head);

	EntryValue getSingle(String key);

	void putSingle(String key, EntryValue value);

	Collection<ResolveResult> resolveMultiple(Collection<EntryHead> keys);

	Map<String, EntryValue> getMultiple(Collection<String> keys);

	void putMultiple(Collection<Entry> entries);

	Collection<String> getAllKeys();

}
