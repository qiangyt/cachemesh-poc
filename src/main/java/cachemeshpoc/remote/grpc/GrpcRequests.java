package cachemeshpoc.remote.grpc;

import com.google.protobuf.ByteString;

public class GrpcRequests {

	public static GetSingleRequest getSingle(String cacheName, String key, long versh) {
		return GetSingleRequest.newBuilder()
				.setCacheName(cacheName)
				.setKey(key)
				.setVersh(versh)
				.build();
	}

	public static PutSingleRequest putSingle(String cacheName, String key, byte[] value) {
		return PutSingleRequest.newBuilder()
				.setCacheName(cacheName)
				.setKey(key)
				.setValue(ByteString.copyFrom(value))
				.build();
	}

}
