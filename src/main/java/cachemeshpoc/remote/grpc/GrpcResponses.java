package cachemeshpoc.remote.grpc;

import io.grpc.stub.StreamObserver;

import cachemeshpoc.GetResult;
import cachemeshpoc.ResultStatus;
import cachemeshpoc.err.CacheMeshServiceException;

public class GrpcResponses {

	public static <V> void complete(StreamObserver<V> observer, V response) {
		observer.onNext(response);
		observer.onCompleted();
		return;
	}

	public static ResultStatus convertStatus(ValueStatus status) {
		switch(status) {
			case NotFound: return ResultStatus.NOT_FOUND;
			case Ok: return ResultStatus.OK;
			case NoChange: return ResultStatus.NO_CHANGE;
			case Redirect: return ResultStatus.REDIRECT;
			default: throw new CacheMeshServiceException("unrecognized remote value status: %d", status);
		}
	}

	public static ValueStatus convertStatus(ResultStatus status) {
		switch(status) {
			case NOT_FOUND: return ValueStatus.NotFound;
			case OK: return ValueStatus.Ok;
			case NO_CHANGE: return ValueStatus.NoChange;
			case REDIRECT: return ValueStatus.Redirect;
			default: throw new CacheMeshServiceException("unrecognized remote value status: %d", status);
		}
	}

	public static GetResult getSingle(GetSingleResponse resp) {
		byte[] v = resp.getValue() == null ? null : resp.getValue().toByteArray();
		return new GetResult(convertStatus(resp.getStatus()), v, resp.getVersh());
	}

}
