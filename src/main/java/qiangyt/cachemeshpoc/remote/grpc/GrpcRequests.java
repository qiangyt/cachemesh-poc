package qiangyt.cachemeshpoc.remote.grpc;

import com.google.protobuf.ByteString;

public class GrpcRequests {

	public static GetSingleRequest getSingle(String cacheName, String key, long version) {
		return GetSingleRequest.newBuilder()
				.setCache(cacheName)
				.setKey(key)
				.setVersion(version)
				.build();
	}

	public static SetSingleRequest setSingle(String cacheName, String key, byte[] value) {
		return SetSingleRequest.newBuilder()
				.setCache(cacheName)
				.setKey(key)
				.setValue(ByteString.copyFrom(value))
				.build();
	}

}
