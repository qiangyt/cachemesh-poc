package cachemeshpoc.local.base;

import cachemeshpoc.local.LocalCache.Config;

@lombok.Getter
@lombok.ToString
@lombok.experimental.SuperBuilder
public abstract class BaseLocalCacheConfig implements Config {

	private final String name;

	private final Class<?> valueClass;

	protected BaseLocalCacheConfig(String name, Class<?> valueClass) {
		this.name = name;
		this.valueClass = valueClass;
	}

}
