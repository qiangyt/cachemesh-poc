package com.github.qiangyt.cachemeshpoc.remote.grpc;


import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

import com.github.qiangyt.cachemeshpoc.remote.RemoteCache;
import com.github.qiangyt.cachemeshpoc.remote.RemoteCacheBuilder;

public class GrpcRemoteCacheBuilder extends GrpcConfigBuilder implements RemoteCacheBuilder {

	@Override
	public RemoteCache build() {
		var cfg = buildConfig();
		var ch = Grpc.newChannelBuilder(cfg.getTarget(), InsecureChannelCredentials.create()).build();
		return new GrpcRemoteCache(cfg, ch);
	}

}
