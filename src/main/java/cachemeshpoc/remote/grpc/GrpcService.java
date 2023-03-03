package cachemeshpoc.remote.grpc;

import io.grpc.stub.StreamObserver;
import cachemeshpoc.ResultStatus;
import cachemeshpoc.SideCacheManager;

import com.google.protobuf.ByteString;

public class GrpcService extends CacheMeshGrpc.CacheMeshImplBase {

	private final SideCacheManager sideCacheManager;

	public GrpcService(SideCacheManager sideCacheManager) {
		this.sideCacheManager = sideCacheManager;
	}

	@Override
	public void getSingle(GetSingleRequest req, StreamObserver<GetSingleResponse> respObserver) {
		var cache = this.sideCacheManager.get(req.getCacheName());
		var respBuilder = GetSingleResponse.newBuilder();
		if (cache == null) {
			respBuilder.setStatus(GrpcResponses.convertStatus(ResultStatus.NOT_FOUND));
		} else {
			var resp = cache.getSingle(req.getKey(), req.getVersh());

			respBuilder.setStatus(GrpcResponses.convertStatus(resp.getStatus()));
			respBuilder.setValue(ByteString.copyFrom(resp.getBytes()));
			respBuilder.setVersh(resp.getVersh());
		}

		GrpcResponses.complete(respObserver, respBuilder.build());
	}

	@Override
	public void putSingle(PutSingleRequest req, StreamObserver<PutSingleResponse> respObserver) {
		var cache = this.sideCacheManager.resolve(req.getCacheName());

		var versh = cache.putSingle(req.getKey(), req.getValue().toByteArray());
		var respBuilder = PutSingleResponse.newBuilder();
		respBuilder.setVersh(versh);
		GrpcResponses.complete(respObserver, respBuilder.build());
	}

}
