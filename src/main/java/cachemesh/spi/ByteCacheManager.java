package cachemesh.spi;

import cachemesh.common.HasName;

public class ByteCacheManager
	extends CommonCacheManager<byte[], ByteCache, ByteCacheConfig>
	implements HasName {

	public ByteCacheManager(ByteCacheConfig defaultConfig) {
		super(defaultConfig);
	}

	@Override
	public String getName() {
		return "bytes";
	}


}
