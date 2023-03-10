package cachemeshpoc.lettuce;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;


import cachemeshpoc.GetResult;
import cachemeshpoc.ResultStatus;


public class LettuceClient implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(LettuceClient.class);

	private static final Base64.Encoder base64Encoder = Base64.getEncoder();

	private static final Base64.Decoder base64Decoder = Base64.getDecoder();

	@lombok.Getter
	private final LettuceConfig config;

	private final RedisClient redis;

	private final StatefulRedisConnection<String,String> conn;

	public LettuceClient(LettuceConfig config, RedisClient redis) {
		this.config = config;
		this.redis = redis;
		this.conn = redis.connect();
	}

	@Override
	public String toString() {
		return this.config.toString();
	}

	void logInfo(String messageFormat, Object... args) {
		String msg = String.format(messageFormat, args);
		LOG.info("{}: {}", this, msg);
	}

	@Override
	public synchronized void close() throws Exception {
		logInfo("shutdown...");

		this.conn.close();
		this.redis.shutdown();

		logInfo("shutdown: done");
	}

	@SuppressWarnings("unchecked")
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var cmds = this.conn.sync();

		String base64 = cmds.get(key);
		if (base64 == null) {
			return (GetResult<byte[]>)GetResult.NOT_FOUND;
		}
		byte[] value = base64Decoder.decode(base64.getBytes(StandardCharsets.UTF_8));

		return new GetResult<>(ResultStatus.OK, value, 0);
	}

	public long putSingle(String cacheName, String key, byte[] value) {
		var cmds = this.conn.sync();

		cmds.set(key, base64Encoder.encodeToString(value));
		return 0;
	}

}
