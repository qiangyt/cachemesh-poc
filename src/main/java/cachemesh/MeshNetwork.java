package cachemesh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;

import cachemesh.caffeine.CaffeineCacheConfig;
import cachemesh.common.Hashing;
import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.hash.MurmurHash;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.common.url.Handler;
import cachemesh.common.util.LogHelper;
import cachemesh.grpc.GrpcCacheManager;
import cachemesh.grpc.GrpcClientBuilder;
import cachemesh.grpc.GrpcConfig;
import cachemesh.grpc.GrpcService;
import cachemesh.lettuce.LettuceCacheManager;
import cachemesh.lettuce.LettuceClientFactory;
import cachemesh.lettuce.LettuceConfig;
import cachemesh.side.SideCacheManager;
import cachemesh.spi.LocalCache;
import cachemesh.spi.LocalCacheConfig;
import cachemesh.spi.LocalCacheManager;
import cachemesh.spi.NodeCache;

public class MeshNetwork implements AutoCloseable, HasName {

	static {
		Handler.registerHandler();
	}

	private final Logger logger = LogHelper.getLogger(this);

	private final ConcurrentHashMap<String, MeshCache<?>> caches = new ConcurrentHashMap<>();

	private final Hashing hashing;

	private final LocalCacheManager<Object> nearCacheManager;

	private final LocalCacheConfig<byte[]> sideCacheDefaultConfig;

	private final ConsistentHash<MeshNode> route;

	private final SortedMap<String, MeshNode> nodes = new TreeMap<>();

	private final Map<URL, GrpcService> grpcServices = new HashMap<>();

	private final GrpcClientBuilder grpcClientBuilder = new GrpcClientBuilder();

	private final LettuceClientFactory lettuceClientFactory = new LettuceClientFactory();

	private final int shutdownSeconds = 60;

	@lombok.Getter
	private boolean inShutdownHook;

	@lombok.Getter
	private boolean bootstrapped;

	@lombok.Getter
	private String name;

	public MeshNetwork(String name) {
		this(name,
			 MurmurHash.DEFAULT,
			 CaffeineCacheConfig.defaultConfig(name + "-local", Object.class, JacksonSerderializer.DEFAULT),
			 CaffeineCacheConfig.defaultConfig(name + "-side", byte[].class, JacksonSerderializer.DEFAULT));
	}

	public MeshNetwork(String name, Hashing hashing, LocalCacheConfig<Object> localCacheDefaultConfig, LocalCacheConfig<byte[]> sideCacheDefaultConfig) {
		this.name = name;

		this.hashing = hashing;
		this.nearCacheManager = new LocalCacheManager<>(localCacheDefaultConfig);
		this.sideCacheDefaultConfig = sideCacheDefaultConfig;

		this.route = new ConsistentHash<>(hashing);

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
			var nearCache = (LocalCache<T>)this.nearCacheManager.resolve(cacheName, (Class<Object>)valueClass);
			return new MeshCache<T>(nearCache, this);
		});
	}

	public MeshNode addLocalNode(String url) throws MalformedURLException {
		return addLocalNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addLocalNode(URL url) {
		var grpcConfig = GrpcConfig.from(url);
		var cacheManager = new SideCacheManager(new LocalCacheManager<byte[]>(this.sideCacheDefaultConfig), this.hashing);
		var grpcService = new GrpcService(grpcConfig, cacheManager);
		this.grpcServices.put(url, grpcService);

		return addNode(new MeshNode(false, url, cacheManager));
	}

	public MeshNode addRemoteNode(String url) throws MalformedURLException {
		return addRemoteNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addRemoteNode(URL url) {
		String protocol = url.getProtocol();
		if (protocol.equals("grpc")) {
			return addGrpcNode(url);
		} else if (protocol.equals("redis")){
			return addRedisNode(url);
		} else {
			throw new InternalException("unsupported protocol: %s", protocol);
		}
	}

	protected MeshNode addGrpcNode(URL url) {
		var grpcConfig = GrpcConfig.from(url);
		var grpcClient = grpcClientBuilder.build(grpcConfig);
		var cacheManager = new GrpcCacheManager(grpcClient);

		return addNode(new MeshNode(true, url, cacheManager));
	}

	protected MeshNode addRedisNode(URL url) {
		var lettuceConfig = LettuceConfig.from(url);
		var lettuceClient = lettuceClientFactory.create(lettuceConfig);
		var cacheManager = new LettuceCacheManager(lettuceClient);

		return addNode(new MeshNode(true, url, cacheManager));
	}

	protected void addNodes(Iterable<MeshNode> nodes) {
		nodes.forEach(this::addNode);
	}

	protected MeshNode addNode(MeshNode node) {
		if (this.nodes.putIfAbsent(node.getKey(), node) != null) {
			throw new InternalException("duplicated node with key=%s", node.getKey());
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
		this.logger.info("find node for key={}: {}", key, node);

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
			throw new InternalException("not bootstrapped");
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
			this.logger.error("shutdown error", err);
		}
	}

	void logShutdown(String messageFormat, Object... args) {
		String msg = String.format(messageFormat, args);
		if (this.inShutdownHook) {
			System.err.println(msg);
		} else {
			this.logger.info(msg);
		}
	}

}
