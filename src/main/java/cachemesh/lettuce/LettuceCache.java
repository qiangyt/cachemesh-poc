package cachemesh.lettuce;

import cachemesh.spi.NodeCache;
import cachemesh.spi.base.GetResult;

import cachemesh.spi.base.ResultStatus;
import io.lettuce.core.api.sync.RedisCommands;


public class LettuceCache implements NodeCache {

	private final LettuceChannel channel;


	public LettuceCache(LettuceChannel channel) {
		this.channel = channel;
	}

	public LettuceConfig getConfig() {
		return this.channel.getConfig();
	}

	public String generateRedisKey(String cacheName, String key) {
		var sep = getConfig().getSeparator();

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
		if (value == null) {
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
