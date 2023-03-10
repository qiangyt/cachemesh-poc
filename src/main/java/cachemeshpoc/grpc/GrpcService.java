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
		logInfo("shutdown..., await %ds", this.config.getServiceShutdownSeconds());

		this.server.shutdown();
		this.awaitTermination(false);

		this.launched = false;
		logInfo("shutdown: done");
	}

	public synchronized void awaitTermination(boolean forever) throws InterruptedException {
		if (forever) {
			this.server.awaitTermination();
		} else {
			this.server.awaitTermination(this.config.getServiceShutdownSeconds(), TimeUnit.SECONDS);
		}
	}

	@Override
	public void getSingle(GetSingleRequest req, StreamObserver<GetSingleResponse> respObserver) {
		LOG.info("getSingle(): cache name={}, key={}, version={}",
					req.getCacheName(), req.getKey(), req.getVersion());

		var cache = this.sideCacheManager.get(req.getCacheName());
		LOG.info("getSingle(): cache={}", cache);

		var respBuilder = GetSingleResponse.newBuilder();
		if (cache == null) {
			respBuilder.setStatus(GrpcHelper.convertStatus(ResultStatus.NOT_FOUND));
		} else {
			var resp = cache.getSingle(req.getKey(), req.getVersion());
			LOG.info("getSingle(): resp={}", resp);

			respBuilder.setStatus(GrpcHelper.convertStatus(resp.getStatus()));
			respBuilder.setVersion(resp.getVersion());

			var value = resp.getValue();
			if (value != null) {
				respBuilder.setValue(ByteString.copyFrom(value));
			}
		}

		GrpcHelper.complete(respObserver, respBuilder.build());
	}

	@Override
	public void putSingle(PutSingleRequest req, StreamObserver<PutSingleResponse> respObserver) {
		LOG.info("putSingle(): cache name={}, key={}", req.getCacheName(), req.getKey());

		var cache = this.sideCacheManager.resolve(req.getCacheName());
		LOG.info("putSingle(): cache={}", cache);

		var value = req.getValue();
		long version;
		if (value == null) {
			version = cache.putSingle(req.getKey(), null);
		} else {
			version = cache.putSingle(req.getKey(), value.toByteArray());
		}
		LOG.info("putSingle(): version={}", version);

		var respBuilder = PutSingleResponse.newBuilder();
		respBuilder.setVersion(version);
		GrpcHelper.complete(respObserver, respBuilder.build());
	}

}
