package cachemesh.lettuce;

import cachemesh.common.shutdown.ShutdownSupport;
import cachemesh.common.shutdown.ShutdownableManager;

public class LettuceChannelManager extends ShutdownableManager<LettuceChannel, LettuceConfig> {

	public static final LettuceChannelManager DEFAULT = new LettuceChannelManager("default-lettuce-channel-manager", ShutdownSupport.DEFAULT);

	public LettuceChannelManager(String name, ShutdownSupport shutdownSupport) {
		super(name, shutdownSupport, 0);
	}

	public LettuceChannelManager(String name, ShutdownSupport shutdownSupport, int shutdownTimeoutSeconds) {
		super(name, shutdownSupport, shutdownTimeoutSeconds);
	}

	@Override
	protected LettuceChannel create(LettuceConfig config) {
		return new LettuceChannel(config, getShutdownSupport(), this);
	}

}
