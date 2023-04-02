package cachemesh.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.LocalCacheConfig;
import cachemesh.core.spi.LocalCacheFactory;
import cachemesh.core.spi.Value;
import lombok.Getter;

@Getter
public class CaffeineFactory implements LocalCacheFactory {

	public static final CaffeineFactory DEFAULT = new CaffeineFactory(ShutdownManager.DEFAULT);

	private final ShutdownManager shutdownManager;

	public CaffeineFactory(ShutdownManager shutdownManager) {
		this.shutdownManager = shutdownManager;
	}

	@Override
	public LocalCache create(LocalCacheConfig config) {
		CaffeineConfig cconfig = (CaffeineConfig)config;
		Cache<String, Value> instance = Caffeine.newBuilder()
									.maximumSize(cconfig.getMaximumSize())
									.expireAfterWrite(cconfig.getExpireAfterWrite())
									.build();
		return new CaffeineCache(cconfig, instance, getShutdownManager());
	}

	@Override
	public String getName() {
		return "caffeine";
	}

}
