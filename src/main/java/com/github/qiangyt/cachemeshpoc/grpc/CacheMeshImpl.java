package com.github.qiangyt.cachemeshpoc.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;


public class CacheMeshImpl extends CacheMeshGrpc.CacheMeshImplBase {

    @Override
    public void getSingle(GetSingleRequest req, StreamObserver<GetSingleReply> responseObserver) {
      var reply = GetSingleReply.newBuilder().setValue(ByteString.copyFromUtf8(req.getKey() + "-value")).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }
