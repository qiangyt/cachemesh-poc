package cachemeshpoc.remote.grpc;

import com.google.protobuf.ByteString;

public class GrpcRequests {

	public static ResolveSingleRequest resolveSingle(String cacheName, String key, long version) {
		return ResolveSingleRequest.newBuilder()
				.setCacheName(cacheName)
				.setKey(key)
				.setVersion(version)
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
