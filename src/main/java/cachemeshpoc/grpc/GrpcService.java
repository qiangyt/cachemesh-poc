package cachemeshpoc.grpc;

import io.grpc.stub.StreamObserver;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.ByteString;


import cachemeshpoc.ResultStatus;
import cachemeshpoc.err.MeshInternalException;
import cachemeshpoc.side.SideCacheManager;

public class GrpcService extends CacheMeshGrpc.CacheMeshImplBase implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcService.class);

	private final SideCacheManager sideCacheManager;

	@lombok.Getter
	private final GrpcConfig config;

	private Server server;

	private boolean launched;

	public GrpcService(GrpcConfig config, SideCacheManager sideCacheManager) {
		this.config = config;
		this.sideCacheManager = sideCacheManager;

		this.server = Grpc.newServerBuilderForPort(this.config.getPort(), InsecureServerCredentials.create())
								.addService(this)
								.build();

		this.launched = false;
	}

	void logInfo(String messageFormat, Object... args) {
		String msg = String.format(messageFormat, args);
		LOG.info("{}: {}", this, msg);
	}

	void raiseError(String messageFormat, Object... args) {
		throw new MeshInternalException("%s: " + messageFormat, this, args);
	}

	void raiseError(Exception cause, String messageFormat, Object... args) {
		throw new MeshInternalException(cause, "%s: " + messageFormat, this, args);
	}

	@Override
	public String toString() {
		return this.config.toString() + "." + this.launched;
	}

	public synchronized void launch() {
		if (this.launched) {
			raiseError("already launched");
		}
		logInfo("launch...");

		try {
			this.server.start();
		} catch (IOException e) {
			raiseError(e, "failed to launch");
		}

		this.launched = true;
		logInfo("launch: done");
	}

	@Override
	public synchronized void close() throws Exception {
		if (!this.launched) {
			raiseError("not launched");
		}
		logInfo("shutdown..., await {}s", this.config.getServiceShutdownSeconds());

		this.server.shutdown().awaitTermination(this.config.getServiceShutdownSeconds(), TimeUnit.SECONDS);

		this.launched = false;
		logInfo("shutdown: done");
	}

	@Override
	public void getSingle(GetSingleRequest req, StreamObserver<GetSingleResponse> respObserver) {
		var cache = this.sideCacheManager.get(req.getCacheName());
		var respBuilder = GetSingleResponse.newBuilder();
		if (cache == null) {
			respBuilder.setStatus(GrpcHelper.convertStatus(ResultStatus.NOT_FOUND));
		} else {
			var resp = cache.getSingle(req.getKey(), req.getVersh());

			respBuilder.setStatus(GrpcHelper.convertStatus(resp.getStatus()));
			respBuilder.setValue(ByteString.copyFrom(resp.getValue()));
			respBuilder.setVersh(resp.getVersh());
		}

		GrpcHelper.complete(respObserver, respBuilder.build());
	}

	@Override
	public void putSingle(PutSingleRequest req, StreamObserver<PutSingleResponse> respObserver) {
		var cache = this.sideCacheManager.resolve(req.getCacheName());

		var versh = cache.putSingle(req.getKey(), req.getValue().toByteArray());
		var respBuilder = PutSingleResponse.newBuilder();
		respBuilder.setVersh(versh);
		GrpcHelper.complete(respObserver, respBuilder.build());
	}

}
