package cachemeshpoc.local.caffeine;

import java.time.Duration;

@lombok.Getter
@lombok.ToString
@lombok.Builder
public class CaffeineGenericCacheConfig {

	public static final int DEFAULT_MAXIMUM_SIZE = 100_000;
	private final int maximumSize;

	public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
	private final Duration expireAfterWrite;

	public static final Duration DEFAULT_REFRESH_AFTER_WRITE = Duration.ofMinutes(1);
	private final Duration refreshAfterWrite;//(Duration.ofMinutes(1))

	public static CaffeineGenericCacheConfig buildDefault() {
		return builder()
				.maximumSize(DEFAULT_MAXIMUM_SIZE)
				.expireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE)
				.refreshAfterWrite(DEFAULT_REFRESH_AFTER_WRITE)
				.build();
	}

}
