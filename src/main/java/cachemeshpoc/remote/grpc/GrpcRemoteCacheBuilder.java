package cachemeshpoc.remote.grpc;


import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

import cachemeshpoc.remote.RemoteCache;
import cachemeshpoc.remote.RemoteCache.Builder;

public class GrpcRemoteCacheBuilder extends GrpcConfigBuilder implements Builder {

	@Override
	public RemoteCache build() {
		var cfg = buildConfig();
		var ch = Grpc.newChannelBuilder(cfg.getTarget(), InsecureChannelCredentials.create()).build();
		return new GrpcClient(cfg, ch);
	}

}
