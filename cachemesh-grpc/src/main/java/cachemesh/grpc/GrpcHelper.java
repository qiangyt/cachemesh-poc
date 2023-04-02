package cachemesh.grpc;

import cachemesh.common.err.ServiceException;
import cachemesh.core.ResultStatus;
import io.grpc.stub.StreamObserver;

public class GrpcHelper {

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
			default: throw new ServiceException("unrecognized remote value status: %d", status);
		}
	}

	public static ValueStatus convertStatus(ResultStatus status) {
		switch(status) {
			case NOT_FOUND: return ValueStatus.NotFound;
			case OK: return ValueStatus.Ok;
			case NO_CHANGE: return ValueStatus.NoChange;
			case REDIRECT: return ValueStatus.Redirect;
			default: throw new ServiceException("unrecognized remote value status: %d", status);
		}
	}

}
