package cachemeshpoc.grpc;

import io.grpc.stub.StreamObserver;
import cachemeshpoc.ResultStatus;
import cachemeshpoc.side.SideCacheManager;

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
