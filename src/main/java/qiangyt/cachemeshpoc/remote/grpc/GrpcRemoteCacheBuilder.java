package qiangyt.cachemeshpoc.remote.grpc;


import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

import qiangyt.cachemeshpoc.remote.RemoteCache;
import qiangyt.cachemeshpoc.remote.RemoteCacheBuilder;

public class GrpcRemoteCacheBuilder extends GrpcConfigBuilder implements RemoteCacheBuilder {

	@Override
	public RemoteCache build() {
		var cfg = buildConfig();
		var ch = Grpc.newChannelBuilder(cfg.getTarget(), InsecureChannelCredentials.create()).build();
		return new GrpcClient(cfg, ch);
	}

}
