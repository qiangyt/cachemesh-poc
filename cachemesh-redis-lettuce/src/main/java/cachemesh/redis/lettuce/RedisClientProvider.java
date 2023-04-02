package cachemesh.lettuce;

import io.lettuce.core.RedisClient;

public interface RedisClientProvider {

	RedisClient get(LettuceConfig config);

	RedisClient resolve(LettuceConfig config);

	RedisClient release(LettuceConfig config, int timeoutSeconds) throws InterruptedException;

}
