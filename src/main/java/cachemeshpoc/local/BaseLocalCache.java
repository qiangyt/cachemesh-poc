package cachemeshpoc.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import cachemeshpoc.ResultStatus;
import cachemeshpoc.local.CacheEntry.Head;
import cachemeshpoc.local.CacheEntry.Value;

@lombok.Getter
@lombok.ToString
public abstract class BaseLocalCache<T> implements LocalCache<T> {

	private final String name;

	private final Class<T> valueClass;

	protected BaseLocalCache(String name, Class<T> valueClass) {
		this.name = name;
		this.valueClass = valueClass;
	}

	@Override
	public void invalidateMultiple(Collection<String> keys) {
		keys.forEach(this::invalidateSingle);
	}

	@Override
	public Result<T> getSingle(Head head) {
		return getSingle(head, this::getSingleAnyhow);
	}

	@SuppressWarnings("unchecked")
	protected Result<T> getSingle(Head head, Function<String, Value<T>> resolver) {
		var v = resolver.apply(head.getKey());
		if (v == null) {
			return (Result<T>)NOT_FOUND;
		}

		if (v.getVersh() == head.getVersh()) {
			return (Result<T>)NO_CHANGE;
		}

		return new Result<T>(ResultStatus.OK, v);
	}

	@Override
	public Collection<Result<T>> getMultiple(Collection<Head> heads) {
		Map<String, Value<T>> entryMap = getMultipleAnyhow(Head.extractKeys(heads));

		var r = new ArrayList<Result<T>>(heads.size());
		heads.forEach(head -> r.add(getSingle(head, entryMap::get)));
		return r;
	}


	@Override
	public void putMultiple(Collection<CacheEntry<T>> entries) {
		entries.forEach(entry -> putSingle(entry.getKey(), entry.getValue()));
	}

}
