package cachemeshpoc.lettuce;

import cachemeshpoc.GetResult;
import cachemeshpoc.NodeCache;

@lombok.Getter
public class LettuceCache implements NodeCache {

	private final String name;

	private final LettuceClient client;

	public LettuceCache(String name, LettuceClient client) {
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
