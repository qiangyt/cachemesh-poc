package cachemesh.caffeine;

import java.time.Duration;
import java.util.Map;

import cachemesh.common.Serderializer;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.core.spi.LocalCacheConfig;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CaffeineConfig extends LocalCacheConfig {

	public static final int DEFAULT_MAXIMUM_SIZE = 100_000;
	private final int maximumSize;

	public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
	private final Duration expireAfterWrite;


	public static CaffeineConfig defaultConfig(String name, Class<?> valueClass) {
		return defaultConfig(name, valueClass, JacksonSerderializer.DEFAULT/*, true*/);
	}


	public static CaffeineConfig defaultConfig(String name,
													Class<?> valueClass,
													Serderializer serder/*,
													boolean cacheBytes*/) {
		var factory = CaffeineFactory.DEFAULT;

		return builder()
				.name(name)
				.valueClass(valueClass)
				.serder(serder)
				//.cacheBytes(cacheBytes)
				.factory(factory)
				.maximumSize(DEFAULT_MAXIMUM_SIZE)
				.expireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE)
				.build();
	}

	public CaffeineConfig(String name,
							   Class<?> valueClass,
							   Serderializer serder,
							   //boolean cacheBytes,
							   CaffeineFactory factory,
							   int maximumSize,
							   Duration expireAfterWrite ) {
		super(name, valueClass, serder, /*cacheBytes,*/ factory);
		this.maximumSize = maximumSize;
		this.expireAfterWrite = expireAfterWrite;
	}

	@Override
	public LocalCacheConfig buildAnother(String name, Class<?> valueClass) {
		return new CaffeineConfig(name, valueClass, getSerder(), /*isCacheBytes(),*/
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
