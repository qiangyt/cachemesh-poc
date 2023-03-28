package cachemesh;

import cachemesh.caffeine.CaffeineConfig;
import cachemesh.common.hash.Hashing;
import cachemesh.common.hash.MurmurHash;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.grpc.GrpcServerManager;
import cachemesh.spi.LocalCacheConfig;

@lombok.Builder
@lombok.Getter
public class MeshNetworkConfig {

	private final String name;

	private Hashing hashing;

	private LocalCacheConfig<Object> nearCacheConfig;

	public MeshNetworkConfig(String name,
							 Hashing hashing,
							 LocalCacheConfig<Object> nearCacheConfig,
							 GrpcServerManager grpcManager) {
		this.name = name;
		this.hashing = hashing;
		this.nearCacheConfig = nearCacheConfig;
	}

	public MeshNetworkConfig(String name) {
		this(name, MurmurHash.DEFAULT,
			 CaffeineConfig.defaultConfig(name + "-nearcache", Object.class, JacksonSerderializer.DEFAULT),
			 GrpcServerManager.DEFAULT);
	}

}
