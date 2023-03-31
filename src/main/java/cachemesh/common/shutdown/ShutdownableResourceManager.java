package cachemesh.common.shutdown;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import cachemesh.common.util.LogHelper;


@ThreadSafe
public abstract class ShutdownableResourceManager<T extends ShutdownableResource<C>, C extends ShutdownableConfig>
	extends AbstractShutdownable {

	private Map<String, T> resources = new ConcurrentHashMap<>();

	public ShutdownableResourceManager(String name, ShutdownSupport shutdownSupport) {
		super(name, shutdownSupport, 0);
	}

	public ShutdownableResourceManager(String name, ShutdownSupport shutdownSupport, int shutdownTimeoutSeconds) {
		super(name, shutdownSupport, shutdownTimeoutSeconds);
	}

	public T resolve(C config) {
		return getResources().computeIfAbsent(config.getName(), target -> {
			var r = create(config);
			getLogger().info("created {}: {}", config.getClass(), LogHelper.entries(r));
			return r;
		});
	}

	protected Map<String, T> getResources() {
		return this.resources;
	}

	protected abstract T create(C config);

	public void remove(ShutdownableResource<C> resource) {
		getResources().remove(resource.getName());
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		var copy = new ArrayList<T>(getResources().values());
		for (var res: copy) {
			res.shutdown(timeoutSeconds);
		}
	}


}
