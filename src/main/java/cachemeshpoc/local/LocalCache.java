package cachemeshpoc.local;

import java.util.Collection;
import java.util.Map;

import cachemeshpoc.Serderializer;
import cachemeshpoc.local.Entry.Head;
import cachemeshpoc.local.Entry.Value;

public interface LocalCache<T> extends AutoCloseable {

	public static interface Config {
		String getName();
		Class<?> getValueClass();
		Serderializer getSerder();
	}

	public static interface Builder {
		<T> LocalCache<T> build(String cacheName);
	}

	public static interface Manager extends AutoCloseable {
		void register(LocalCache<?> cache);
		<T> LocalCache<T> get(String cacheName);
		<T> LocalCache<T> resolve(String cacheName);
	}


	Config getConfig();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	LocalResult<T> resolveSingle(Head head);

	Value<T> getSingle(String key);

	void putSingle(String key, Value<T> value);

	Collection<LocalResult<T>> resolveMultiple(Collection<Head> keys);

	Map<String, Value<T>> getMultiple(Collection<String> keys);

	void putMultiple(Collection<Entry<T>> entries);

	Collection<String> getAllKeys();

}
