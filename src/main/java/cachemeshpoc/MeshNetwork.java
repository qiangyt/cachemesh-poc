package cachemeshpoc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemeshpoc.caffeine.CaffeineCacheFactory;
import cachemeshpoc.err.MeshInternalException;
import cachemeshpoc.grpc.GrpcCacheManager;
import cachemeshpoc.grpc.GrpcClientFactory;
import cachemeshpoc.grpc.GrpcConfig;
import cachemeshpoc.grpc.GrpcService;
import cachemeshpoc.json.JsonSerderializer;
import cachemeshpoc.lettuce.LettuceCacheManager;
import cachemeshpoc.lettuce.LettuceClientFactory;
import cachemeshpoc.lettuce.LettuceConfig;
import cachemeshpoc.local.LocalCacheFactory;
import cachemeshpoc.local.LocalCacheManager;
import cachemeshpoc.side.SideCacheManager;
import cachemeshpoc.url.Handler;
import cachemeshpoc.util.ConsistentHash;
import cachemeshpoc.util.MurmurHash;

public class MeshNetwork implements AutoCloseable {

	static {
		Handler.registerHandler();
	}

	private static final Logger LOG = LoggerFactory.getLogger(MeshNetwork.class);

	private final ConcurrentHashMap<String, MeshCache<?>> caches = new ConcurrentHashMap<>();

	private final LocalCacheManager nearCacheManager;

	private final ConsistentHash<MeshNode> route;

	private final Map<String, MeshNode> nodes = new HashMap<>();

	private final Map<URL, GrpcService> grpcServices = new HashMap<>();

	private final Serderializer serder;

	private final GrpcClientFactory grpcClientFactory = new GrpcClientFactory();

	private final LettuceClientFactory lettuceClientFactory = new LettuceClientFactory();

	private final LocalCacheFactory localCacheFactory;

	private final int shutdownSeconds = 60;

	@lombok.Getter
	private boolean inShutdownHook;

	@lombok.Getter
	private boolean bootstrapped;

	public MeshNetwork() {
		this(new CaffeineCacheFactory(), JsonSerderializer.DEFAULT);
	}

	public MeshNetwork(LocalCacheFactory localCacheFactory, Serderializer serder) {
		this.localCacheFactory = localCacheFactory;
		this.route = new ConsistentHash<>(MurmurHash.DEFAULT);
		this.nearCacheManager = new LocalCacheManager(localCacheFactory);
		this.serder = serder;

		this.bootstrapped = false;
		this.inShutdownHook = false;
	}

	@Override
	public String toString() {
		return this.nodes.values().toArray().toString();
	}

	@SuppressWarnings("unchecked")
	public <T> MeshCache<T> getCache(String cacheName) {
		return (MeshCache<T>) this.caches.get(cacheName);
	}

	@SuppressWarnings("unchecked")
	public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
		return (MeshCache<T>) this.caches.computeIfAbsent(cacheName, k -> {
			var nearCache = this.nearCacheManager.resolve(cacheName, VershedValue.class);
			return new MeshCache<T>(valueClass, nearCache, this, serder);
		});
	}

	public MeshNode addLocalNode(String url) throws MalformedURLException {
		return addLocalNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addLocalNode(URL url) {
		var grpcConfig = GrpcConfig.from(url);
		var cacheManager = new SideCacheManager(this.localCacheFactory);
		var grpcService = new GrpcService(grpcConfig, cacheManager);
		this.grpcServices.put(url, grpcService);

		return addNode(new MeshNode(false, url, cacheManager));
	}

	public MeshNode addRemoteNode(String url) throws MalformedURLException {
		return addRemoteNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addRemoteNode(URL url) {
		var grpcConfig = GrpcConfig.from(url);
		var grpcClient = grpcClientFactory.create(grpcConfig);
		var cacheManager = new GrpcCacheManager(grpcClient);

		return addNode(new MeshNode(true, url, cacheManager));
	}

	public MeshNode addRedisNode(String url) throws MalformedURLException {
		return addRedisNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addRedisNode(URL url) {
		var lettuceConfig = LettuceConfig.from(url);
		var lettuceClient = lettuceClientFactory.create(lettuceConfig);
		var cacheManager = new LettuceCacheManager(lettuceClient);

		return addNode(new MeshNode(true, url, cacheManager));
	}

	public void addNodes(Iterable<MeshNode> nodes) {
		nodes.forEach(this::addNode);
	}

	public MeshNode addNode(MeshNode node) {
		if (this.nodes.putIfAbsent(node.getKey(), node) != null) {
			throw new MeshInternalException("duplicated node with key=%s", node.getKey());
		}

		this.route.join(node);
		return node;
	}

	public MeshNode findNode(String key) {
		long hash = this.route.hash(key);
		var virtualNode = this.route.virtualNodeFor(hash);
		return virtualNode.getRealNode();
	}

	public NodeCache resolveNodeCache(String cacheName, String key) {
		var node = findNode(key);
		LOG.info("find node for key={}: {}", key, node);

		var nodeCacheMgr = node.getNodeCacheManager();
		return nodeCacheMgr.resolve(cacheName);
	}

	void shutdownGrpcServers() {

		logShutdown("grpc service shutdown...");

		var grpcServices = this.grpcServices.values();
		var latch = new CountDownLatch(grpcServices.size());

		grpcServices.forEach(grpcService -> {
			new Thread(() -> {
				try {
					grpcService.close();
				} catch (Exception e) {
					logShutdownError(e);
				}
			}).start();
			latch.countDown();
		});

		try {
			latch.await(this.shutdownSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logShutdownError(e);
		}

		logShutdown("grpc service shutdown: done");
	}

	public synchronized void bootstrap() {
		this.grpcServices.values().forEach(GrpcService::launch);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				MeshNetwork.this.inShutdownHook = true;
				try {
					close();
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			}
		}));
	}

	public void blockUntilTermination(boolean forever) {
		this.grpcServices.values().forEach((grpcService) -> {
			try {
				grpcService.awaitTermination(forever);
			} catch (InterruptedException ex) {
				// logShutdown("mesh network shutdown...");
				ex.printStackTrace(System.err);
			}
		});
	}

	@Override
	public synchronized void close() throws Exception {
		if (this.bootstrapped) {
			throw new MeshInternalException("not bootstrapped");
		}
		logShutdown("mesh network shutdown...");

		shutdownGrpcServers();

		logShutdown("nearcache shutdown: ...");
		this.nearCacheManager.close();
		logShutdown("nearcache shutdown: done");

		this.bootstrapped = false;
		logShutdown("mesh network shutdown: done");
	}

	void logShutdownError(Exception err) {
		if (this.inShutdownHook) {
			err.printStackTrace(System.err);
		} else {
			LOG.error("shutdown error", err);
		}
	}

	void logShutdown(String messageFormat, Object... args) {
		String msg = String.format(messageFormat, args);
		if (this.inShutdownHook) {
			System.err.println(msg);
		} else {
			LOG.info(msg);
		}
	}

}
