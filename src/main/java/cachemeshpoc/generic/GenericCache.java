package cachemeshpoc.generic;

import java.util.Collection;
import java.util.Map;

import cachemeshpoc.generic.GenericEntry.Head;
import cachemeshpoc.generic.GenericEntry.Status;
import cachemeshpoc.generic.GenericEntry.Value;

public interface GenericCache<T> extends AutoCloseable {

	public static interface Factory {
		<T> GenericCache<T> create(String cacheName, Class<T> valueClass);
	}

	@lombok.Getter
	@lombok.ToString
	public static class Result<T> {
		private final Status status;
		private final Value<T> value;

		public Result(Status status, Value<T> value) {
			this.status = status;
			this.value = value;
		}
	}


	public static final Result<?> NOT_FOUND = new Result<>(Status.NOT_FOUND, null);
	public static final Result<?> NO_CHANGE = new Result<>(Status.NO_CHANGE, null);

	String getName();

	Class<T> getValueClass();

	void invalidateSingle(String key);

	void invalidateMultiple(Collection<String> keys);

	Result<T> getSingle(Head head);

	Value<T> getSingleAnyhow(String key);

	Collection<Result<T>> getMultiple(Collection<Head> keys);

	Map<String, Value<T>> getMultipleAnyhow(Collection<String> keys);

	void putSingle(String key, Value<T> value);

	void putMultiple(Collection<GenericEntry<T>> entries);

	Collection<String> getAllKeys();

}
