package cachemesh.caffeine;

import java.time.Duration;
import java.util.Map;

import cachemesh.common.Serderializer;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.spi.CommonCacheConfig;

@lombok.Getter
@lombok.experimental.SuperBuilder
public class CaffeineCacheConfig<T> extends CommonCacheConfig<T> {

	public static final int DEFAULT_MAXIMUM_SIZE = 100_000;
	private final int maximumSize;

	public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
	private final Duration expireAfterWrite;


	public static <T> CaffeineCacheConfig<T> defaultConfig(String name, Class<T> valueClass) {
		return defaultConfig(name, valueClass, JacksonSerderializer.DEFAULT);
	}


	@SuppressWarnings("unchecked")
	public static <T> CaffeineCacheConfig<T> defaultConfig(String name,
													Class<T> valueClass,
													Serderializer serder) {
		var factory = CaffeineCacheFactory.DEFAULT;

		return (CaffeineCacheConfig<T>)builder()
				.name(name)
				.valueClass((Class<Object>)valueClass)
				.serder(serder)
				.factory(factory)
				.maximumSize(DEFAULT_MAXIMUM_SIZE)
				.expireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE)
				.build();
	}

	public CaffeineCacheConfig(String name,
							   Class<T> valueClass,
							   Serderializer serder,
							   CaffeineCacheFactory factory,
							   int maximumSize,
							   Duration expireAfterWrite ) {
		super(name, valueClass, serder, factory);
		this.maximumSize = maximumSize;
		this.expireAfterWrite = expireAfterWrite;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T2, C2 extends CommonCacheConfig<T2>> C2 buildAnother(String name, Class<T2> valueClass) {
		return (C2)new CaffeineCacheConfig<>(name, valueClass,  getSerder(),
									   (CaffeineCacheFactory)getFactory(),
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
