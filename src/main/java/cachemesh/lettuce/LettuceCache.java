package cachemesh.lettuce;

import cachemesh.core.GetResult;
import cachemesh.core.ResultStatus;
import cachemesh.spi.NodeCache;
import io.lettuce.core.api.sync.RedisCommands;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownSupport;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;

@Getter
public class LettuceCache extends AbstractShutdownable implements NodeCache {

	private final StatefulRedisConnection<String,byte[]> connection;

	private final RedisClient client;

	private final LettuceConfig config;

	public LettuceCache(LettuceConfig config, ShutdownSupport shutdownSupport) {
		super(config.getTarget(), shutdownSupport);

		this.config = config;
		this.client = RedisClient.create(config.getTarget());
		this.connection = this.client.connect(LettuceCodec.DEFAULT);
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		try {
			getConnection().close();
		} finally {
			getClient().shutdown();
		}
	}

	public String generateRedisKey(String cacheName, String key) {
		var sep = getConfig().getKeySeparator();

		return new StringBuilder(cacheName.length() + sep.length() + key.length())
					.append(cacheName)
					.append(sep)
					.append(key)
					.toString();
	}

	RedisCommands<String, byte[]> syncCommand() {
		return getConnection().sync();
	}

	@Override
	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var redisKey = generateRedisKey(cacheName, key);

		var cmds = syncCommand();
		var value = cmds.get(redisKey);
		if (value == null) {//TODO: how to indicate we do have the value but the value is null
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		return new GetResult<>(ResultStatus.OK, value, 0);
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		var redisKey = generateRedisKey(cacheName, key);

		var cmds = syncCommand();
		cmds.set(redisKey, value);

		return 0;
	}

}
