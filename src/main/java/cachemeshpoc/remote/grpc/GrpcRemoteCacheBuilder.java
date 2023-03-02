package cachemeshpoc.remote.grpc;


import cachemeshpoc.RawCache;
import cachemeshpoc.RawCache.Builder;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

public class GrpcRemoteCacheBuilder extends GrpcConfigBuilder implements Builder {

	@Override
	public RawCache build() {
		var cfg = buildConfig();
		var ch = Grpc.newChannelBuilder(cfg.getTarget(), InsecureChannelCredentials.create()).build();
		return new GrpcClient(cfg, ch);
	}

}
