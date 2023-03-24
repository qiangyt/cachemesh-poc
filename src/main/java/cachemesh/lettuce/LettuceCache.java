package cachemesh.lettuce;

import cachemesh.common.util.LogHelper;
import cachemesh.spi.NodeCache;
import cachemesh.spi.base.GetResult;

import org.slf4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.kv;

import cachemesh.spi.base.ResultStatus;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;


@lombok.Getter
public class LettuceCache implements NodeCache {

	private final Logger logger;

	@lombok.Getter
	private final LettuceConfig config;

	private final StatefulRedisConnection<String,byte[]> conn;

	private final RedisClient client;


	public LettuceCache(LettuceConfig config) {
		this.config = config;
		this.logger = LogHelper.getLogger(this);
		this.client = RedisClient.create(config.getTarget());
		this.conn = this.client.connect(config.getCodec());
	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

	public String generateRedisKey(String cacheName, String key) {
		var sep = getConfig().getSeparator();

		return new StringBuilder(cacheName.length() + sep.length() + key.length())
					.append(cacheName)
					.append(sep)
					.append(key)
					.toString();
	}

	@Override
	public synchronized void close() throws Exception {
		var nameKv = kv("name", getName());
		this.logger.info("{}: shutdowning ...", nameKv);

		this.conn.close();
		this.client.shutdown();

		this.logger.info("{}: shutdown done", nameKv);
	}

	@Override
	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var cmds = this.conn.sync();

		var redisKey = generateRedisKey(cacheName, key);
		var value = cmds.get(redisKey);
		if (value == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}

		return new GetResult<>(ResultStatus.OK, value, 0);
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		var cmds = this.conn.sync();
		cmds.set(key, value);
		return 0;
	}
}
