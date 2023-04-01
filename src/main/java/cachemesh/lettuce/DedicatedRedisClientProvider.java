package cachemesh.lettuce;

import java.time.Duration;

import cachemesh.common.Manager;
import io.lettuce.core.RedisClient;


public class DedicatedRedisClientProvider
	extends Manager<LettuceConfig, RedisClient>
	implements RedisClientProvider {

	@Override
	protected String retrieveKey(LettuceConfig config) {
		return config.getTarget();
	}

	@Override
	protected RedisClient doCreate(LettuceConfig config) {
		return RedisClient.create(config.getTarget());
	}

	@Override
	protected void doRelease(LettuceConfig config, RedisClient client, int timeoutSeconds)
		throws InterruptedException {
		var timeout = Duration.ofSeconds(timeoutSeconds);
		client.shutdown(timeout, timeout);
	}

}
