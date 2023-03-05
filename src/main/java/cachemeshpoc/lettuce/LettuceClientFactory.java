package cachemeshpoc.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class LettuceClientFactory {

	public LettuceClient create(LettuceConfig config) {

		var redisClient = RedisClient.create(config.getTarget());
		StatefulRedisConnection<String, String> conn = redisClient.connect();
		RedisCommands<String, String> syncCommands = conn.sync();

		syncCommands.set("key", "Hello, Redis!");


		return new LettuceClient(config, redisClient);
	}

}
