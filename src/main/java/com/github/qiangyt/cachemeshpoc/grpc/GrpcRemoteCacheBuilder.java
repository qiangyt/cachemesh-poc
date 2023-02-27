package com.github.qiangyt.cachemeshpoc.grpc;


import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

import com.github.qiangyt.cachemeshpoc.RemoteCache;
import static com.github.qiangyt.cachemeshpoc.grpc.GrpcRemoteCacheConfig.GrpcRemoteCacheConfigBuilder;

public class GrpcRemoteCacheBuilder extends GrpcRemoteCacheConfigBuilder implements RemoteCache.Builder {

	@Override
	public RemoteCache build() {
		var config = buildConfig();
		var channel = Grpc.newChannelBuilder(config.target(), InsecureChannelCredentials.create()).build();
		return new GrpcRemoteCache(config, channel);
	}

}
