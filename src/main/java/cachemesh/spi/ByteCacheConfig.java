package cachemesh.spi;

public class ByteCacheConfig extends CommonCacheConfig<byte[]> {

	public ByteCacheConfig(String name, ByteCacheFactory factory) {
		super(name, byte[].class, null, factory);
	}

}
