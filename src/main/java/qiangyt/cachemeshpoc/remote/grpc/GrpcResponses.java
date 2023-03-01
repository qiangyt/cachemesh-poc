package qiangyt.cachemeshpoc.remote.grpc;

import io.grpc.stub.StreamObserver;
import qiangyt.cachemeshpoc.err.CacheMeshServiceException;
import qiangyt.cachemeshpoc.remote.ResolveSingleResult;
import qiangyt.cachemeshpoc.remote.RemoteValueStatus;

public class GrpcResponses {

	public static <V> void complete(StreamObserver<V> observer, V response) {
		observer.onNext(response);
		observer.onCompleted();
		return;
	}

	public static RemoteValueStatus convertValueStatus(ValueStatus status) {
		switch(status) {
			case NotFound: return RemoteValueStatus.NotFound;
			case Changed: return RemoteValueStatus.Changed;
			case NoChange: return RemoteValueStatus.NoChange;
			case Redirect: return RemoteValueStatus.Redirect;
			default: throw new CacheMeshServiceException("unrecognized remote value status: %d", status);
		}
	}

	public static ResolveSingleResult resolveSingle(ResolveSingleResponse resp) {
		return ResolveSingleResult.builder()
				.status(convertValueStatus(resp.getStatus()))
				.value(resp.getValue() == null ? null : resp.getValue().toByteArray())
				.version(resp.getVersion())
				.build();
	}

}
