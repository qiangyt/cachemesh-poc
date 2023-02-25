package com.github.qiangyt.cachemeshpoc.grpc;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CacheMeshClient {
  private static final Logger LOG = Logger.getLogger(CacheMeshClient.class.getName());

  private final CacheMeshGrpc.CacheMeshBlockingStub stub;

  public CacheMeshClient(Channel channel) {
    this.stub = CacheMeshGrpc.newBlockingStub(channel);
  }

  /** Say hello to server. */
  public void getSingle(String key) {
    LOG.info("Will try to put single key " + key + " ...");
    var req = GetSingleRequest.newBuilder().setKey(key).build();
    GetSingleReply resp;
    try {
      resp = stub.getSingle(req);
    } catch (StatusRuntimeException e) {
      LOG.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    LOG.info("Response: " + resp.getValue().toStringUtf8());
  }

}
