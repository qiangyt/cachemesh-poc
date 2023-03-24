package cachemesh.grpc;

import cachemesh.common.util.LogHelper;
import cachemesh.spi.NodeCache;
import cachemesh.spi.base.GetResult;

import io.grpc.ManagedChannel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

import java.util.concurrent.TimeUnit;

import static net.logstash.logback.argument.StructuredArguments.kv;
import org.slf4j.Logger;

import com.google.protobuf.ByteString;


public class GrpcCache implements NodeCache {

	private final Logger logger;

	@lombok.Getter
	private final GrpcConfig config;

	private final ManagedChannel channel;

	private final CacheMeshGrpc.CacheMeshBlockingStub stub;


	public GrpcCache(GrpcConfig config) {
		this.config = config;
		this.logger = LogHelper.getLogger(this);
		this.channel = Grpc.newChannelBuilder(config.getTarget(), InsecureChannelCredentials.create()).build();
		this.stub = CacheMeshGrpc.newBlockingStub(channel);
	}

	@Override
	public String getName() {
		return getConfig().getName();
	}

	@Override
	public synchronized void close() throws Exception {
		var nameKv = kv("name", getName());
		this.logger.info("{}: shutdowning ..., {}", nameKv, kv("timeout", this.config.getServiceShutdownSeconds() + "s"));

		this.channel.shutdownNow().awaitTermination(this.config.getClientShutdownSeconds(), TimeUnit.SECONDS);

		this.logger.info("{}: shutdown done", nameKv);
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

		var v = (resp.getValue() == null) ? null : resp.getValue().toByteArray();
		return new GetResult<>(GrpcHelper.convertStatus(resp.getStatus()), v, resp.getVersion());
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		var req = PutSingleRequest.newBuilder()
					.setCacheName(cacheName)
					.setKey(key)
					.setValue(ByteString.copyFrom(value))
					.build();
		var resp = this.stub.putSingle(req);
		return resp.getVersion();
	}

}
