package cachemeshpoc.caffeine;

import java.time.Duration;

@lombok.Getter
@lombok.ToString
@lombok.Builder
public class CaffeineCacheConfig {

	public static final int DEFAULT_MAXIMUM_SIZE = 100_000;
	private final int maximumSize;

	public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
	private final Duration expireAfterWrite;

}
