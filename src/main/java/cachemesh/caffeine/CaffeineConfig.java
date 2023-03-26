package cachemesh.caffeine;

import java.time.Duration;
import java.util.Map;

import cachemesh.common.Serderializer;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.spi.LocalCacheConfig;

@lombok.Getter
@lombok.experimental.SuperBuilder
public class CaffeineConfig<T> extends LocalCacheConfig<T> {

	public static final int DEFAULT_MAXIMUM_SIZE = 100_000;
	private final int maximumSize;

	public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
	private final Duration expireAfterWrite;


	public static <T> CaffeineConfig<T> defaultConfig(String name, Class<T> valueClass) {
		return defaultConfig(name, valueClass, JacksonSerderializer.DEFAULT, true);
	}


	@SuppressWarnings("unchecked")
	public static <T> CaffeineConfig<T> defaultConfig(String name,
													Class<T> valueClass,
													Serderializer serder,
													boolean cacheBytes) {
		var factory = CaffeineFactory.DEFAULT;

		return (CaffeineConfig<T>)builder()
				.name(name)
				.valueClass((Class<Object>)valueClass)
				.serder(serder)
				.cacheBytes(cacheBytes)
				.factory(factory)
				.maximumSize(DEFAULT_MAXIMUM_SIZE)
				.expireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE)
				.build();
	}

	public CaffeineConfig(String name,
							   Class<T> valueClass,
							   Serderializer serder,
							   boolean cacheBytes,
							   CaffeineFactory factory,
							   int maximumSize,
							   Duration expireAfterWrite ) {
		super(name, valueClass, serder, cacheBytes, factory);
		this.maximumSize = maximumSize;
		this.expireAfterWrite = expireAfterWrite;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T2, C2 extends LocalCacheConfig<T2>> C2 buildAnother(String name, Class<T2> valueClass) {
		return (C2)new CaffeineConfig<>(name, valueClass, getSerder(), isCacheBytes(),
									   (CaffeineFactory)getFactory(),
									   getMaximumSize(),
									   getExpireAfterWrite());
	}

	@Override
	public Map<String, Object> toMap() {
		var r = super.toMap();

		r.put("maximumSize", getMaximumSize());
		r.put("expireAfterWrite", getExpireAfterWrite());

		return r;
	}

}
