package com.github.qiangyt.cachemeshpoc.remote.grpc;

import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.qiangyt.cachemeshpoc.remote.GetSingleResult;
import com.github.qiangyt.cachemeshpoc.remote.RemoteCache;

public class GrpcRemoteCache implements RemoteCache {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcRemoteCache.class);

	@lombok.Getter
	private final GrpcConfig serverConfig;

	private final ManagedChannel channel;

	private final CacheMeshGrpc.CacheMeshBlockingStub stub;

	public GrpcRemoteCache(GrpcConfig serverConfig, ManagedChannel channel) {
		this.serverConfig = serverConfig;
		this.channel = channel;
		this.stub = CacheMeshGrpc.newBlockingStub(channel);
	}

	@Override
	public void close() throws Exception {
		LOG.info("Shut down: ...");
		this.channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		LOG.info("Shutdown: done");
	}

	@Override
	public GetSingleResult getSingle(String cacheName, String key, long version) {
		var req = GrpcRequests.getSingle(cacheName, key, version);

		var resp = this.stub.getSingle(req);
		/*try {
			resp = stub.getSingle(req);
		} catch (StatusRuntimeException e) {
			LOG.warn("Get single key RPC failed: {}", e.getStatus());
			return null;
		} */

		return GrpcResponses.getSingle(resp);
	}

	@Override
	public long setSingle(String cacheName, String key, byte[] value) {
		var req = GrpcRequests.setSingle(cacheName, key, value);
		var resp = this.stub.setSingle(req);
		return resp.getVersion();
	}

}
