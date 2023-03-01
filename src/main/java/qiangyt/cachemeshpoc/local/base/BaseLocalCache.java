package qiangyt.cachemeshpoc.local.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import qiangyt.cachemeshpoc.local.Entry;
import qiangyt.cachemeshpoc.local.LocalCache;
import qiangyt.cachemeshpoc.local.LocalCacheConfig;
import qiangyt.cachemeshpoc.local.ResolveResult;
import qiangyt.cachemeshpoc.local.ValueStatus;
import qiangyt.cachemeshpoc.local.EntryHead;
import qiangyt.cachemeshpoc.local.EntryValue;

public abstract class BaseLocalCache implements LocalCache {

	@lombok.Getter
	private final LocalCacheConfig config;

	protected BaseLocalCache(LocalCacheConfig config) {
		this.config = config;
	}

	@Override
	public void invalidateMultiple(Collection<String> keys) {
		keys.forEach(this::invalidateSingle);
	}

	@Override
	public ResolveResult resolveSingle(EntryHead head) {
		return resolveSingle(head, this::getSingle);
	}

	ResolveResult resolveSingle(EntryHead head, Function<String, EntryValue> resolver) {
		var value = resolver.apply(head.getKey());
		if (value == null) {
			return ResolveResult.NOT_FOUND;
		}

		if (value.getVersion() <= head.getVersion()) {
			//removeSingle(existingHead);
			return ResolveResult.NO_CHANGE;
		}

		return ResolveResult.builder()
				.status(ValueStatus.Changed).value(value).build();
	}

	@Override
	public Collection<ResolveResult> resolveMultiple(Collection<EntryHead> heads) {
		var entryMap = getMultiple(EntryHead.keys(heads));

		var r = new ArrayList<ResolveResult>(heads.size());
		for (var head: heads) {
			var s = resolveSingle(head, entryMap::get);
			r.add(s);
		}

		return r;
	}

	@Override
	public void putMultiple(Collection<Entry> entries) {
		entries.forEach(entry -> putSingle(entry.getKey(), entry.getValue()));
	}

}
