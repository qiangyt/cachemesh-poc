package cachemeshpoc.local.base;

import cachemeshpoc.local.LocalCacheConfig;

@lombok.Getter
@lombok.ToString
@lombok.experimental.SuperBuilder
public abstract class BaseLocalCacheConfig implements LocalCacheConfig {

	private final String name;

	private final Class<?> valueClass;

	protected BaseLocalCacheConfig(String name, Class<?> valueClass) {
		this.name = name;
		this.valueClass = valueClass;
	}

}
