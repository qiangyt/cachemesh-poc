package cachemesh.lettuce;

import cachemesh.core.GetResult;
import cachemesh.core.ResultStatus;
import cachemesh.spi.NodeCache;
import io.lettuce.core.api.sync.RedisCommands;


public class LettuceNodeCache implements NodeCache {

	private final LettuceChannel channel;

	public LettuceNodeCache(LettuceChannel channel) {
		this.channel = channel;
	}

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		this.channel.shutdown(timeoutSeconds);
	}

	public LettuceConfig getConfig() {
		return this.channel.getConfig();
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
		return this.channel.getConn().sync();
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
