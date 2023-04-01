package cachemesh.common;

public abstract class Manager<C, T> extends Registry<C, T> {

	public T resolve(C config) {
		String key = retrieveKey(config);
		return getItemMap().computeIfAbsent(key, k -> doCreate(config));
	}

	public T create(C config) {
		String key = retrieveKey(config);
		return getItemMap().compute(key, (k, existing) -> {
			if (existing != null) {
				throw new IllegalArgumentException("duplicated: " + key);
			}
			return doCreate(config);
		});
	}

	public T release(C config, int timeoutSeconds) throws InterruptedException {
		T r = unregister(config);
		if (r != null) {
			doRelease(config, r, timeoutSeconds);
		}
		return r;
	}

	protected abstract T doCreate(C config);

	protected abstract void doRelease(C config, T item, int timeoutSeconds) throws InterruptedException;

}
