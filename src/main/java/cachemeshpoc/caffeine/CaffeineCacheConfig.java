package cachemeshpoc.caffeine;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@lombok.Getter
@lombok.ToString
@lombok.Builder
public class CaffeineCacheConfig {

	public static final int DEFAULT_MAXIMUM_SIZE = 100_000;
	private final int maximumSize;

	public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
	private final Duration expireAfterWrite;

	public static CaffeineCacheConfig defaultConfig() {
		return builder()
				.maximumSize(DEFAULT_MAXIMUM_SIZE)
				.expireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE)
				.build();
	}

	public <T> Cache<String, T> create() {
		return Caffeine.newBuilder()
							.maximumSize(getMaximumSize())
							.expireAfterWrite(getExpireAfterWrite())
							.build();
	}

}
