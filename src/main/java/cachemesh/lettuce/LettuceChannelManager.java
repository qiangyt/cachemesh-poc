package cachemesh.lettuce;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.util.LogHelper;

public class LettuceChannelManager {

	public static final LettuceChannelManager DEFAULT = new LettuceChannelManager(ShutdownSupport.DEFAULT);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, LettuceChannel> channels = new ConcurrentHashMap<>();

	private ShutdownSupport shutdown;

	public LettuceChannelManager(ShutdownSupport shutdown) {
		this.shutdown = shutdown;
	}

	public LettuceChannel resolve(LettuceConfig config) {
		var target = config.getTarget();

		return this.channels.computeIfAbsent(target, k -> {
			var r = new LettuceChannel(config);
			if (this.shutdown != null) {
				this.shutdown.register(r);
			}

			this.logger.info("created lettuce channel: {}", LogHelper.entries(r));
			return r;
		});
	}

}
