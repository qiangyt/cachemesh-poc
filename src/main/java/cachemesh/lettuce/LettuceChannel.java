package cachemesh.lettuce;

import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.shutdown.ShutdownableResource;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

@lombok.Getter
public class LettuceChannel extends ShutdownableResource<LettuceConfig> {

	private final StatefulRedisConnection<String,byte[]> conn;

	private final RedisClient client;

	public LettuceChannel(LettuceConfig config, ShutdownSupport shutdownSupport, LettuceChannelManager channelManager) {
		super(config, shutdownSupport, channelManager);

		this.client = RedisClient.create(config.getName());
		this.conn = this.client.connect(LettuceCodec.DEFAULT);
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
