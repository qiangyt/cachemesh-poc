package cachemesh.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;

public class ByteCacheConfig extends LocalCacheConfig<byte[]> {

	public ByteCacheConfig(String name, ByteCacheFactory factory) {
		super(name, byte[].class, null, false, factory);
	}

}
