package cachemeshpoc.remote.grpc;

import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemeshpoc.MeshResult;
import cachemeshpoc.RawCache;

public class GrpcClient implements RawCache {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcClient.class);

	@lombok.Getter
	private final GrpcConfig serverConfig;

	private final ManagedChannel channel;

	private final CacheMeshGrpc.CacheMeshBlockingStub stub;

	public GrpcClient(GrpcConfig serverConfig, ManagedChannel channel) {
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
	public MeshResult resolveSingle(String cacheName, String key, long version) {
		var req = GrpcRequests.resolveSingle(cacheName, key, version);

		var resp = this.stub.resolveSingle(req);
		/*try {
			resp = stub.getSingle(req);
		} catch (StatusRuntimeException e) {
			LOG.warn("Get single key RPC failed: {}", e.getStatus());
			return null;
		} */

		return GrpcResponses.resolveSingle(resp);
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		var req = GrpcRequests.putSingle(cacheName, key, value);
		var resp = this.stub.putSingle(req);
		return resp.getVersion();
	}

}
