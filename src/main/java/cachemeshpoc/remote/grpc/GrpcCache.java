package cachemeshpoc.remote.grpc;

import cachemeshpoc.GetResult;
import cachemeshpoc.NodeCache;

public class GrpcCache implements NodeCache {

	@lombok.Getter
	private final String name;

	private final GrpcClient client;

	public GrpcCache(String name, GrpcClient client) {
		this.name = name;
		this.client = client;
	}

	@Override
	public GetResult getSingle(String key, long versh) {
		return this.client.getSingle(this.name, key, versh);
	}

	@Override
	public long putSingle(String key, byte[] bytes) {
		return this.client.putSingle(key, key, bytes);
	}

}
