package cachemesh.grpc;

import java.util.concurrent.TimeUnit;

import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import io.grpc.ManagedChannel;
import lombok.Getter;

import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.core.GetResult;
import cachemesh.spi.Transport;

import com.google.protobuf.ByteString;

@Getter
public class GrpcTransport extends AbstractShutdownable implements Transport {

	private CacheMeshGrpc.CacheMeshBlockingStub stub;

	private ManagedChannel channel;

	private final GrpcConfig config;

	public GrpcTransport(GrpcConfig config, ShutdownManager shutdownManager) {
		super(config.getTarget(), shutdownManager);

		this.config = config;
	}

	@Override
	public boolean isRemote() {
		return getConfig().isRemote();
	}

	@Override
	public void start(int timeoutSeconds) throws InterruptedException {
		var ch = getConfig().createClientChannel();
		this.channel = ch;
		this.stub = CacheMeshGrpc.newBlockingStub(ch);
	}

	@Override
	public void stop(int timeoutSeconds) throws InterruptedException {
		shutdown(timeoutSeconds);
	}

	@Override
	public void onShutdown(ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
		var ch = getChannel();
		ch.shutdown();

		shutdownLogger.info("await termination");
		ch.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
	}

	@Override
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var req = GetSingleRequest.newBuilder()
					.setCacheName(cacheName)
					.setKey(key)
					.setVersion(version)
					.build();

		var resp = this.stub.getSingle(req);
		/*try {
			resp = stub.getSingle(req);
		} catch (StatusRuntimeException e) {
			LOG.warn("Get single key RPC failed: {}", e.getStatus());
			return null;
		} */

		//TODO: how to indicate we do have the value but the value is null
		var respV = resp.getValue();
		var v = (respV == null) ? null : respV.toByteArray();
		return new GetResult<>(GrpcHelper.convertStatus(resp.getStatus()), v, resp.getVersion());
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		var req = PutSingleRequest.newBuilder()
					.setCacheName(cacheName)
					.setKey(key);
		if (value != null) {
			req.setValue(ByteString.copyFrom(value));
		}

		var resp = this.stub.putSingle(req.build());
		return resp.getVersion();
	}

}
