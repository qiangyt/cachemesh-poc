package com.github.qiangyt.cachemeshpoc;

import java.util.concurrent.TimeUnit;

import com.github.qiangyt.cachemeshpoc.remote.grpc.GrpcRemoteCache;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;


public class Main {

  public static void main(String[] args) throws Exception {
    var ch = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
    try {
      var client = new GrpcRemoteCache(ch);
      client.getSingle("key_1");
    } finally {
      ch.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

}
