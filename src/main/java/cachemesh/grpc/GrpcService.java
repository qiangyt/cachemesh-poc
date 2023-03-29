package cachemesh.grpc;

import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

import cachemesh.spi.NodeCache;

public class GrpcService extends CacheMeshGrpc.CacheMeshImplBase {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcService.class);

	private final NodeCache cache;


	public GrpcService(NodeCache cache) {
		this.cache = cache;
	}


	@Override
	public void getSingle(GetSingleRequest req, StreamObserver<GetSingleResponse> respObserver) {
		var cacheName = req.getCacheName();
		var key = req.getKey();
		var ver = req.getVersion();
		var debug = LOG.isDebugEnabled();

		if (debug) {
			LOG.debug("getSingle(): cache name={}, key={}, version={}", cacheName, key, ver);
		}

		var resp = this.cache.getSingle(cacheName, key, ver);
		if (debug) {
			LOG.debug("getSingle(): resp={}", resp);
		}

		var respBuilder = GetSingleResponse.newBuilder();
		respBuilder.setStatus(GrpcHelper.convertStatus(resp.getStatus()));
		respBuilder.setVersion(resp.getVersion());

		var value = resp.getValue();
		if (value != null) {
			respBuilder.setValue(ByteString.copyFrom(value));
		}

		GrpcHelper.complete(respObserver, respBuilder.build());
	}


	@Override
	public void putSingle(PutSingleRequest req, StreamObserver<PutSingleResponse> respObserver) {
		var cacheName = req.getCacheName();
		var key = req.getKey();
		var value = req.getValue();
		var debug = LOG.isDebugEnabled();

		if (debug) {
			LOG.debug("putSingle(): cache name={}, key={}", cacheName, key);
		}

		var ver = this.cache.putSingle(cacheName, key, value.toByteArray());
		if (debug) {
			LOG.debug("putSingle(): version={}", ver);
		}

		var respBuilder = PutSingleResponse.newBuilder();
		respBuilder.setVersion(ver);
		GrpcHelper.complete(respObserver, respBuilder.build());
	}

}
