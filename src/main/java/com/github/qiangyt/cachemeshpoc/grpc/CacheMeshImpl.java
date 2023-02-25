package com.github.qiangyt.cachemeshpoc.grpc;

import io.grpc.stub.StreamObserver;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.google.protobuf.ByteString;

public class CacheMeshImpl extends CacheMeshGrpc.CacheMeshImplBase {

	private static final Logger LOG = LoggerFactory.getLogger(CacheMeshImpl.class);

	@Override
	public void getSingle(GetSingleRequest req, StreamObserver<GetSingleReply> responseObserver) {
		var reply = GetSingleReply.newBuilder().setValue(ByteString.copyFromUtf8(req.getKey() + "-value")).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
}
