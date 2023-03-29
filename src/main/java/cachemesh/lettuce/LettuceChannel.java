package cachemesh.lettuce;

import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

@lombok.Getter
public class LettuceChannel extends AbstractShutdownable {

	private final LettuceConfig config;

	private final StatefulRedisConnection<String,byte[]> conn;

	private final RedisClient client;

	public LettuceChannel(LettuceConfig config) {
		super(config.getUrl());

		this.config = config;
		this.client = RedisClient.create(config.getTarget());
		this.conn = this.client.connect(config.getCodec());

		setShutdownNeeded(true);
	}

	@Override
	public String toString() {
		return getConfig().toString();
	}


	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		try {
			this.conn.close();
		} finally {
			this.client.shutdown();
		}
	}

}
