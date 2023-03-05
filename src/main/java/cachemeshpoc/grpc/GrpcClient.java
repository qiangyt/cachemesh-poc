package cachemeshpoc.grpc;

import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

import cachemeshpoc.GetResult;

public class GrpcClient implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcClient.class);

	@lombok.Getter
	private final GrpcConfig config;

	private final ManagedChannel channel;

	private final CacheMeshGrpc.CacheMeshBlockingStub stub;

	public GrpcClient(GrpcConfig config, ManagedChannel channel) {
		this.config = config;
		this.channel = channel;
		this.stub = CacheMeshGrpc.newBlockingStub(channel);
	}

	@Override
	public String toString() {
		return this.config.toString();
	}

	void logInfo(String messageFormat, Object... args) {
		String msg = String.format(messageFormat, args);
		LOG.info("{}: {}", this, msg);
	}

	@Override
	public synchronized void close() throws Exception {
		logInfo("shutdown..., await {}s", this.config.getClientShutdownSeconds());

		this.channel.shutdownNow().awaitTermination(this.config.getClientShutdownSeconds(), TimeUnit.SECONDS);

		logInfo("shutdown: done");
	}

	public GetResult<byte[]> getSingle(String cacheName, String key, long versh) {
		var req = GetSingleRequest.newBuilder()
					.setCacheName(cacheName)
					.setKey(key)
					.setVersh(versh)
					.build();

		var resp = this.stub.getSingle(req);
		/*try {
			resp = stub.getSingle(req);
		} catch (StatusRuntimeException e) {
			LOG.warn("Get single key RPC failed: {}", e.getStatus());
			return null;
		} */

		byte[] v = resp.getValue() == null ? null : resp.getValue().toByteArray();
		return new GetResult<>(GrpcHelper.convertStatus(resp.getStatus()), v, resp.getVersh());
	}

	public long putSingle(String cacheName, String key, byte[] value) {
		var req = PutSingleRequest.newBuilder()
					.setCacheName(cacheName)
					.setKey(key)
					.setValue(ByteString.copyFrom(value))
					.build();
		var resp = this.stub.putSingle(req);
		return resp.getVersh();
	}

}
