package cachemesh.spi.base;

import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;

public abstract class BaseCache<T, C extends LocalCacheConfig<T>, I> implements LocalCache<T> {

	@lombok.Getter
	protected final C config;

	@lombok.Getter
	protected final I instance;

	public BaseCache(C cfg, I instance) {
		this.config = cfg;
		this.instance = instance;
	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

}
