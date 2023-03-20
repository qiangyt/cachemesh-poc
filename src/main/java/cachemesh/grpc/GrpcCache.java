package cachemesh.grpc;

import cachemesh.spi.NodeCache;
import cachemesh.spi.base.GetResult;

@lombok.Getter
public class GrpcCache implements NodeCache {

	private final String name;

	private final GrpcClient client;

	public GrpcCache(String name, GrpcClient client) {
		this.name = name;
		this.client = client;
	}

	@Override
	public GetResult<byte[]> getSingle(String key, long version) {
		return this.client.getSingle(this.name, key, version);
	}

	@Override
	public long putSingle(String key, byte[] bytes) {
		return this.client.putSingle(this.name, key, bytes);
	}

}
