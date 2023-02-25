package com.github.qiangyt.cachemeshpoc;

import java.util.concurrent.TimeUnit;

import com.github.qiangyt.cachemeshpoc.grpc.CacheMeshClient;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;


public class Main {

  public static void main(String[] args) throws Exception {
    var channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create()).build();
    try {
      var client = new CacheMeshClient(channel);
      client.getSingle("key_1");
    } finally {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

}
