package cachemesh;

import java.util.concurrent.ConcurrentHashMap;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;

import cachemesh.common.HasName;
import cachemesh.common.err.InternalException;
import cachemesh.common.hash.ConsistentHash;
import cachemesh.common.url.Handler;
import cachemesh.common.util.LogHelper;
import cachemesh.grpc.GrpcClientManager;
import cachemesh.grpc.GrpcConfig;
import cachemesh.grpc.GrpcNodeCache;
import cachemesh.grpc.GrpcService;
import cachemesh.grpc.GrpcServerManager;
import cachemesh.lettuce.LettuceChannelManager;
import cachemesh.lettuce.LettuceConfig;
import cachemesh.lettuce.LettuceNodeCache;
import cachemesh.spi.LocalCacheManager;
import cachemesh.spi.base.LocalNodeCache;
import lombok.Getter;

public class MeshNetwork implements HasName {

	static {
		Handler.registerHandler();
	}

	@Getter
	private final MeshNetworkConfig config;

	@Getter
	private final LocalCacheManager nearCacheManager;

	@Getter
	private final LocalCacheManager localCacheManager;

	@Getter
	private final GrpcServerManager grpcServerManager;

	@Getter
	private final GrpcClientManager grpcClientManager;

	@Getter
	private final LettuceChannelManager lettuceChannelManager;

	@lombok.Getter
	private boolean bootstrapped;

	private final ConsistentHash<MeshNode> route;

	private final Logger logger;

	private final ConcurrentHashMap<String, MeshCache<?>> caches;

	public MeshNetwork(MeshNetworkConfig config,
						LocalCacheManager nearCacheManager,
						LocalCacheManager localCacheManager,
						GrpcServerManager grpcServerManager,
						GrpcClientManager grpcClientManager,
						LettuceChannelManager lettuceChannelManager) {
		this.config = config;
		this.nearCacheManager = nearCacheManager;
		this.localCacheManager = localCacheManager;
		this.grpcServerManager = grpcServerManager;
		this.grpcClientManager = grpcClientManager;
		this.lettuceChannelManager = lettuceChannelManager;

		this.bootstrapped = false;
		this.route = new ConsistentHash<>(config.getHashing());
		this.logger = LogHelper.getLogger(this);
		this.caches = new ConcurrentHashMap<>();
	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

	@SuppressWarnings("unchecked")
	public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
		return (MeshCache<T>) this.caches.computeIfAbsent(cacheName, k -> {
			return new MeshCache<>(cacheName, getLocalCacheManager(), this);
		});
	}

	public MeshNode addLocalNode(String url) throws MalformedURLException {
		return addLocalNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addLocalNode(URL url) {
		var grpcConfig = GrpcConfig.from(url);
		var grpcServer = getGrpcServerManager().resolve(grpcConfig);
		var localNodeCache = new LocalNodeCache(getLocalCacheManager());
		var grpcService = new GrpcService(localNodeCache);
		grpcServer.addService(grpcService);

		var node = new MeshNode(false, url, localNodeCache);

		return addNode(node);
	}

	public MeshNode addRemoteNode(String url) throws MalformedURLException {
		return addRemoteNode(new URL(null, url, Handler.DEFAULT));
	}

	public MeshNode addRemoteNode(URL url) {
		String protocol = url.getProtocol();
		if (protocol.equals("grpc")) {
			return addGrpcNode(url);
		} else if (protocol.equals("redis")) {
			return addRedisNode(url);
		} else {
			throw new InternalException("unsupported protocol: %s", protocol);
		}
	}

	protected MeshNode addGrpcNode(URL url) {
		var grpcConfig = GrpcConfig.from(url);
		var grpcClient = getGrpcClientManager().resolve(grpcConfig);
		var grpcCache = new GrpcNodeCache(grpcClient);

		var node = new MeshNode(true, url, grpcCache);

		return addNode(node);
	}

	protected MeshNode addRedisNode(URL url) {
		var lettuceConfig = LettuceConfig.from(url);
		var lettuceChannel = getLettuceChannelManager().resolve(lettuceConfig);
		var lettuceCache = new LettuceNodeCache(lettuceChannel);

		var node = new MeshNode(true, url, lettuceCache);

		return addNode(node);
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

	public void bootstrap() {
		if (this.bootstrapped) {
			throw new InternalException("already bootstrapped");
		}

		this.logger.info("mesh network bootstrap: ...");

		getGrpcServerManager().startAll();

		this.bootstrapped = true;
		this.logger.info("mesh network bootstrap: done");
	}

	public void shutdown() throws InterruptedException {
		if (this.bootstrapped) {
			throw new InternalException("not bootstrapped");
		}

		this.logger.info("mesh network shutdown...");

		// TODO

		this.bootstrapped = false;
		this.logger.info("mesh network shutdown: done");
	}

}
