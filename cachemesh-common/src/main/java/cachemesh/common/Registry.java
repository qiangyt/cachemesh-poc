package cachemesh.common;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class Registry<C, T> {

	@Getter(AccessLevel.PROTECTED)
	private Map<String, T> itemMap;

	protected Map<String, T> createItemMap() {
		return new HashMap<>();
	}

	public void register(C config, T item) {
		var key = retrieveKey(config);
		getItemMap().compute(key, (k, existing) -> {
			if (existing != null) {
				throw new IllegalArgumentException("duplicated: " + key);
			}
			return item;
		});
	}

	public T unregister(C config) {
		String key = retrieveKey(config);
		return getItemMap().remove(key);
	}

	public T get(C config) {
		String key = retrieveKey(config);
		return getByKey(key);
	}

	public T getByKey(String key) {
		return getItemMap().get(key);
	}

	protected abstract String retrieveKey(C config);

}
