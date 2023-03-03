package cachemeshpoc.local;

import java.util.Collection;
import java.util.Map;

import cachemeshpoc.ResultStatus;
import cachemeshpoc.local.CacheEntry.Head;
import cachemeshpoc.local.CacheEntry.Value;

public interface LocalCache<T> extends AutoCloseable {

	public static interface Factory {
		<T> LocalCache<T> create(String cacheName, Class<T> valueClass);
	}

	@lombok.Getter
	@lombok.ToString
	public static class Result<T> {
		private final ResultStatus status;
		private final Value<T> value;

		public Result(ResultStatus status, Value<T> value) {
			this.status = status;
			this.value = value;
		}
	}


	public static final Result<?> NOT_FOUND = new Result<>(ResultStatus.NOT_FOUND, null);
	public static final Result<?> NO_CHANGE = new Result<>(ResultStatus.NO_CHANGE, null);

	String getName();

	Class<T> getValueClass();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	Result<T> getSingle(Head head);

	Value<T> getSingleAnyhow(String key);

	Collection<Result<T>> getMultiple(Collection<Head> keys);

	Map<String, Value<T>> getMultipleAnyhow(Collection<String> keys);

	void putSingle(String key, Value<T> value);

	void putMultiple(Collection<CacheEntry<T>> entries);

	Collection<String> getAllKeys();

}
