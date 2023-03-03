package cachemeshpoc.remote.grpc;

import io.grpc.stub.StreamObserver;

import cachemeshpoc.GetResult;
import cachemeshpoc.GetResult.Status;
import cachemeshpoc.err.CacheMeshServiceException;

public class GrpcResponses {

	public static <V> void complete(StreamObserver<V> observer, V response) {
		observer.onNext(response);
		observer.onCompleted();
		return;
	}

	public static Status convertStatus(ValueStatus status) {
		switch(status) {
			case NotFound: return Status.NOT_FOUND;
			case Ok: return Status.OK;
			case NoChange: return Status.NO_CHANGE;
			case Redirect: return Status.REDIRECT;
			default: throw new CacheMeshServiceException("unrecognized remote value status: %d", status);
		}
	}

	public static ValueStatus convertStatus(Status status) {
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
