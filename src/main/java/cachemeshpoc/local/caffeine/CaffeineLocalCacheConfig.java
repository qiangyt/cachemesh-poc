package cachemeshpoc.local.caffeine;

import java.time.Duration;

import cachemeshpoc.local.base.BaseLocalCacheConfig;

@lombok.Getter
@lombok.ToString
@lombok.experimental.SuperBuilder
public class CaffeineLocalCacheConfig extends BaseLocalCacheConfig {

	public static final int DEFAULT_MAXIMUM_SIZE = 100_000;
	private final int maximumSize;

	public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
	private final Duration expireAfterWrite;

	public static final Duration DEFAULT_REFRESH_AFTER_WRITE = Duration.ofMinutes(1);
	private final Duration refreshAfterWrite;//(Duration.ofMinutes(1))

	public static CaffeineLocalCacheConfig buildDefault(String name) {
		return builder()
				.name(name)
				.valueClass(null)
				.maximumSize(DEFAULT_MAXIMUM_SIZE)
				.expireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE)
				.refreshAfterWrite(DEFAULT_REFRESH_AFTER_WRITE)
				.build();
	}

}
