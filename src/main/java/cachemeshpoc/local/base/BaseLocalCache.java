package cachemeshpoc.local.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import cachemeshpoc.local.Entry;
import cachemeshpoc.local.LocalCache;
import cachemeshpoc.local.LocalResult;
import cachemeshpoc.local.Entry.Head;
import cachemeshpoc.local.Entry.Status;
import cachemeshpoc.local.Entry.Value;


public abstract class BaseLocalCache<T> implements LocalCache<T> {

	@lombok.Getter
	private final Config config;

	protected BaseLocalCache(Config config) {
		this.config = config;
	}

	@Override
	public void invalidateMultiple(Collection<String> keys) {
		keys.forEach(this::invalidateSingle);
	}

	@Override
	public LocalResult<T> resolveSingle(Head head) {
		return resolveSingle(head, this::getSingle);
	}

	@SuppressWarnings("unchecked")
	LocalResult<T> resolveSingle(Head head, Function<String, Value<T>> resolver) {
		var v = resolver.apply(head.getKey());
		if (v == null) {
			return (LocalResult<T>)LocalResult.NOT_FOUND;
		}

		if (v.getVersion() == head.getVersion()) {
			return (LocalResult<T>)LocalResult.NO_CHANGE;
		}

		return new LocalResult<T>(Status.CHANGED, v);
	}

	@Override
	public Collection<LocalResult<T>> resolveMultiple(Collection<Head> heads) {
		var entryMap = getMultiple(Head.extractKeys(heads));

		var r = new ArrayList<LocalResult<T>>(heads.size());
		heads.forEach(head -> r.add(resolveSingle(head, entryMap::get)));
		return r;
	}

	@Override
	public void putMultiple(Collection<Entry<T>> entries) {
		entries.forEach(entry -> putSingle(entry.getKey(), entry.getValue()));
	}

}
