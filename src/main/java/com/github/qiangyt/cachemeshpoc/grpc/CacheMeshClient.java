package com.github.qiangyt.cachemeshpoc.grpc;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheMeshClient {

	private static final Logger LOG = LoggerFactory.getLogger(CacheMeshClient.class);

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
      LOG.warn("RPC failed: {0}", e.getStatus());
      return;
    }
    LOG.info("Response: " + resp.getValue().toStringUtf8());
  }

}
