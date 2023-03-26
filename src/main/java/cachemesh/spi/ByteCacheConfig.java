package cachemesh.spi;

public class ByteCacheConfig extends LocalCacheConfig<byte[]> {

	public ByteCacheConfig(String name, ByteCacheFactory factory) {
		super(name, byte[].class, null, false, factory);
	}

}
