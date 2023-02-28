package qiangyt.cachemeshpoc.remote.grpc;

import qiangyt.cachemeshpoc.CacheMeshException;
import qiangyt.cachemeshpoc.remote.GetSingleResult;
import qiangyt.cachemeshpoc.remote.RemoteValueStatus;

public class GrpcResponses {

	public static RemoteValueStatus convertValueStatus(ValueStatus status) {
		switch(status) {
			case NotFound: return RemoteValueStatus.NotFound;
			case Changed: return RemoteValueStatus.Changed;
			case NoChange: return RemoteValueStatus.NoChange;
			case Redirect: return RemoteValueStatus.Redirect;
			default: throw new CacheMeshException("unrecognized remote value status: %d", status);
		}
	}

	public static GetSingleResult getSingle(GetSingleResponse resp) {
		return GetSingleResult.builder()
				.status(convertValueStatus(resp.getStatus()))
				.value(resp.getValue() == null ? null : resp.getValue().toByteArray())
				.version(resp.getVersion())
				.build();
	}

}
