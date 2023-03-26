package cachemesh.common.shutdown;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ShutdownSupport {

	public static final int DEFAULT_TIMEOUT_SECONDS = 2;

	public static final ShutdownSupport DEFAULT = new ShutdownSupport();

	private SortedMap<String, ShutdownItem> items = new TreeMap<>();

	@lombok.Getter
	private int maxTimeoutSeconds;

	private Thread thread;

	private final ShutdownLogger logger = new ShutdownLogger(ShutdownSupport.class);


	public void register(Shutdownable target) {
		register(target, DEFAULT_TIMEOUT_SECONDS);
	}

	public void register(Iterable<Shutdownable> targets) {
		targets.forEach(this::register);
	}

	public void register(Iterable<Shutdownable> targets, int timeoutSeconds) {
		targets.forEach(target -> register(target, timeoutSeconds));
	}

	public void register(Shutdownable target, int timeoutSeconds) {
		var name = target.getName();

		this.items.compute(name, (k, existing) -> {
			if (existing != null) {
				throw new IllegalStateException(name + "already registered");
			}
			return new ShutdownItem(this, target, timeoutSeconds);
		});

		target.onShutdownRegistered(this);
		this.logger.info("registered shutdown: %s", name);


		refreshTimeout();
	}

	public void unregister(Shutdownable target) {
		var name = target.getName();

		this.items.compute(name, (k, existing) -> {
			if (existing == null) {
				throw new IllegalStateException(name + "not yet registered");
			}
			return null;
		});

		this.logger.info("unregistered shutdown: %s", name);

		refreshTimeout();
	}

	public void refreshTimeout() {
		int timeout = 0;
		for (var i : this.items.values()) {
			timeout = Math.max(timeout, i.getTimeoutSeconds());
		}
		;
		this.maxTimeoutSeconds = timeout;
	}

	public void unhook() {
		if (this.thread == null) {
			throw new IllegalStateException("not ever enabled");
		}
		if (!Runtime.getRuntime().removeShutdownHook(this.thread)) {
			throw new IllegalStateException("failed to remove shutdown hook");
		}

		this.logger.info("shutdown hook is removed");
	}

	public void hook() {
		if (this.thread != null) {
			throw new IllegalStateException("already enabled");
		}

		this.thread = new Thread(() -> {
			this.logger.setInShutdownHook(true);

			try {
				shutdownAll();
			} catch (InterruptedException e) {
				this.logger.error(e, "shutdown is interrupted");
				e.printStackTrace();
			}
		});

		Runtime.getRuntime().addShutdownHook(this.thread);
		this.logger.info("shutdown hook is added");
	}


	public void shutdown(Shutdownable target, int timeoutSeconds) {
		String name = target.getName();
		if (target.isShutdownNeeded() == false) {
			throw new IllegalStateException(name + " no need shutdown");
		}

		var item = this.items.get(name);
		if (item == null) {
			throw new IllegalStateException(name + " is not ever registered");
		}
		if (item.isShutdowned()) {
			this.items.remove(name);
			throw new IllegalStateException(name + " is already shutdowned");
		}


		var th = new Thread(() -> item.shutdown(timeoutSeconds));
		th.start();

		var itemLogger = item.getLogger();
		itemLogger.info("waiting item to shutdown. timeout seconds=%is", timeoutSeconds);
		try {
			th.join(item.getTimeoutSeconds() * 1000);
		} catch (InterruptedException e) {
			itemLogger.error(e, "error occurred when joining the interrupted");
		}

		if (item.isShutdowned() == false) {
			itemLogger.info("shutdown timeout, interrupt it now");
			th.interrupt();

			try {
				th.join(100);
			} catch (InterruptedException e) {
				itemLogger.error(e, "error occurred when joining the interrupted");
			}
		}

		this.items.remove(name);
	}


	public void shutdownAll() throws InterruptedException {
		this.logger.info("Shutdown begin");

		var currItems = new ArrayList<>(this.items.values());
		currItems.removeIf(item -> !item.isShutdownNeeded());

		var latch = new CountDownLatch(currItems.size());

		var threads = new ArrayList<Thread>(currItems.size());
		currItems.forEach(item -> {
			threads.add(new Thread(() -> {
				item.shutdown(0);
				latch.countDown();
			}));
		});

		threads.forEach(Thread::start);

		this.logger.info("waiting %i item to shutdown. timeout seconds=%is",
				currItems.size(), this.maxTimeoutSeconds);
		latch.await(this.maxTimeoutSeconds, TimeUnit.SECONDS);
		this.logger.info("wait timeout %i", currItems.size());

		for (int i = 0; i < currItems.size(); i++ ) {
			var item = currItems.get(i);
			if (item.isShutdowned() == false) {
				item.getLogger().info("shutdown timeout, interrupt it now");
				threads.get(i).interrupt();
			}
		}

		for (int i = 0; i < currItems.size(); i++ ) {
			var item = currItems.get(i);
			if (item.isShutdowned() == false) {
				var iLogger = item.getLogger();
				iLogger.info("waiting for being interrupted for shutdown");
				try {
					threads.get(i).join(100);
				} catch (InterruptedException e) {
					iLogger.error(e, "error occurred when joining the interrupted");
				}
			}
		}

		var newItems = new TreeMap<String, ShutdownItem>();
		currItems.forEach(item -> {
			if (item.isShutdownNeeded()) {
				newItems.put(item.getTarget().getName(), item);
			}
		});
		this.items = newItems;

		this.logger.info("Shutdown finished");
	}

}
