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
import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.hash.Hashing;
import cachemesh.common.hash.MurmurHash;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.common.url.Handler;
import cachemesh.common.util.LogHelper;
import cachemesh.grpc.GrpcConfig;
import cachemesh.grpc.GrpcService;
import cachemesh.grpc.GrpcServerManager;
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

	private final MeshNetworkConfig config;

	private final LocalCacheManager<Object> nearCacheManager;

	private final LocalCacheManager<byte[]> sideCacheManager;

	private final ConsistentHash<MeshNode> route;

	@lombok.Getter
	private boolean bootstrapped = false;

	public MeshNetwork(MeshNetworkConfig config) {
		this.config = config;

		this.nearCacheManager = new LocalCacheManager<>(config.getNearCacheConfig());
		this.sideCacheManager = new LocalCacheManager<>(config.getSideCacheDefaultConfig());

		this.route = new ConsistentHash<>(config.getHashing());
	}

	@SuppressWarnings("unchecked")
	public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
		return (MeshCache<T>) this.caches.computeIfAbsent(cacheName, k -> {
			var nearCache = (LocalCache<T>)this.nearCacheManager.resolve(cacheName, (Class<Object>)valueClass);
			return new MeshCache<>(nearCache, this);
		});
	}

	public MeshNode addLocalNode(String url) throws MalformedURLException {
		return addLocalNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addLocalNode(URL url) {
		var nodeCache = this.sideCacheManager.resolve(null, byte[].class);
		var cacheManager = new SideCacheManager(new LocalCacheManager<byte[]>(this.sideCacheDefaultConfig), this.hashing);

		var grpcConfig = GrpcConfig.from(url);
		var grpcServer = this.config.getGrpcManager().resolve(grpcConfig);

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
