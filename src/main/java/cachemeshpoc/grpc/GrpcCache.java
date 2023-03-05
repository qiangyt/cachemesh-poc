package cachemeshpoc.grpc;

import cachemeshpoc.GetResult;
import cachemeshpoc.NodeCache;

@lombok.Getter
public class GrpcCache implements NodeCache {

	private final String name;

	private final GrpcClient client;

	public GrpcCache(String name, GrpcClient client) {
		this.name = name;
		this.client = client;
	}

	@Override
	public GetResult<byte[]> getSingle(String key, long versh) {
		return this.client.getSingle(this.name, key, versh);
	}

	@Override
	public long putSingle(String key, byte[] bytes) {
		return this.client.putSingle(this.name, key, bytes);
	}

}
