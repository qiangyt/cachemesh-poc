package cachemesh.spi.base;

import cachemesh.spi.CommonCache;
import cachemesh.spi.CommonCacheConfig;

public abstract class BaseCommonCache<T, C extends CommonCacheConfig<T>, I> implements CommonCache<T, C> {

	@lombok.Getter
	protected final C config;

	@lombok.Getter
	protected final I instance;

	protected BaseCommonCache(C cfg, I instance) {
		this.config = cfg;
		this.instance = instance;
	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

}
