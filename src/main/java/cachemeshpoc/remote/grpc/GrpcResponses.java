package cachemeshpoc.remote.grpc;

import io.grpc.stub.StreamObserver;
import cachemeshpoc.MeshResult;
import cachemeshpoc.MeshResult.Status;
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
			case Changed: return Status.OK;
			case NoChange: return Status.NO_CHANGE;
			case Redirect: return Status.REDIRECTED;
			default: throw new CacheMeshServiceException("unrecognized remote value status: %d", status);
		}
	}

	public static MeshResult resolveSingle(ResolveSingleResponse resp) {
		byte[] v = resp.getValue() == null ? null : resp.getValue().toByteArray();
		return new MeshResult(convertStatus(resp.getStatus()), v, resp.getVersion());
	}

}
