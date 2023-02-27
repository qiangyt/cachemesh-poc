package com.github.qiangyt.cachemeshpoc.grpc;

import io.grpc.stub.StreamObserver;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.github.qiangyt.cachemeshpoc.MeshCache;
import com.github.qiangyt.cachemeshpoc.MeshCacheManager;
import com.google.protobuf.ByteString;

public class CacheMeshImpl extends CacheMeshGrpc.CacheMeshImplBase {

	private static final Logger LOG = LoggerFactory.getLogger(CacheMeshImpl.class);

	private final NearMeshCacheManager cacheManager;

	public CacheMeshImpl(MeshCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public void getSingle(GetSingleRequest req, StreamObserver<GetSingleReply> resp) {
		String cache = req.getCache();
		String key = req.getKey();

		this.cacheManager.get(null, null);
		var reply = GetSingleReply.newBuilder().setValue(ByteString.copyFromUtf8(req.getKey() + "-value")).build();
		resp.onNext(reply);
		resp.onCompleted();
	}
}
