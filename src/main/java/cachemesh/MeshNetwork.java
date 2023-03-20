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
import cachemesh.grpc.GrpcManager;
import cachemesh.grpc.GrpcService;
import cachemesh.lettuce.LettuceCacheManager;
import cachemesh.lettuce.LettuceClientFactory;
import cachemesh.lettuce.LettuceConfig;
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

	private final GrpcManager grpcManager = new GrpcManager();

	private final LettuceClientFactory lettuceClientFactory = new LettuceClientFactory();

	@lombok.Getter
	private String name;

	@lombok.Getter
	private boolean bootstrapped = false;

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
		var cacheManager = new SideCacheManager(new LocalCacheManager<byte[]>(this.sideCacheDefaultConfig), this.hashing);

		var grpcConfig = GrpcConfig.from(url);
		this.grpcManager.createService(grpcConfig, cacheManager);

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
		var grpcClient = this.grpcManager.buildClient(grpcConfig);
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
		this.route.join(node);
		return node;
	}

	public MeshNode findNode(String key) {
		return this.route.findNode(key);
	}

	@Override
	public synchronized void close() throws Exception {
		if (this.bootstrapped) {
			throw new InternalException("not bootstrapped");
		}

		this.logger.info("mesh network shutdown...");

		this.grpcManager.shutdown();

		this.logger.info("nearcache shutdown: ...");
		this.nearCacheManager.close();
		this.logger.info("nearcache shutdown: done");

		this.bootstrapped = false;
		this.logger.info("mesh network shutdown: done");
	}

	public synchronized void bootstrap() {

	}

}
