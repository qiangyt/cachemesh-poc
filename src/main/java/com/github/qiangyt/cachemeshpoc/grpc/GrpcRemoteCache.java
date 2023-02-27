package com.github.qiangyt.cachemeshpoc.grpc;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.qiangyt.cachemeshpoc.RemoteCache;
import com.google.protobuf.ByteString;

public class GrpcRemoteCache implements RemoteCache {

	private static final Logger LOG = LoggerFactory.getLogger(GrpcRemoteCache.class);

	@lombok.Getter
	private final GrpcRemoteCacheConfig config;

	private final ManagedChannel channel;

	private final CacheMeshGrpc.CacheMeshBlockingStub stub;

	public GrpcRemoteCache(GrpcRemoteCacheConfig config, ManagedChannel channel) {
		this.config = config;
		this.channel = channel;
		this.stub = CacheMeshGrpc.newBlockingStub(channel);
	}

	@Override
	public void close() throws Exception {
		LOG.info("Shut down: ...");
		this.channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		LOG.info("Shutdown: done");
	}

	@Override
	public byte[] getSingle(String key) {
		boolean debug = LOG.isDebugEnabled();
		if (debug) {
			LOG.debug("Get single key: key={}", key);
		}

		var req = GetSingleRequest.newBuilder().setKey(key).build();
		GetSingleReply resp;
		try {
			resp = stub.getSingle(req);
		} catch (StatusRuntimeException e) {
			LOG.warn("Get single key RPC failed: {}", e.getStatus());
			return null;
		}

		ByteString r = resp.getValue();
		if (debug) {
			if (r == null) {
				LOG.debug("Get single key response: null");
			} else {
				LOG.debug("Get single key response: ", r.toStringUtf8());
			}
		}

		return r.toByteArray();
	}

	@Override
	public void setSingle(String key, byte[] value) {
		boolean debug = LOG.isDebugEnabled();
		if (debug) {
			LOG.debug("Set single key: {}, {} bytes value", key, value.length);
		}

		var req = SetSingleRequest.newBuilder().setKey(key).setValue(ByteString.copyFrom(value)).build();
		SetSingleReply resp;
		try {
			resp = stub.setSingle(req);
		} catch (StatusRuntimeException e) {
			LOG.warn("Set single key RPC failed: {}", e.getStatus());
			return;
		}
	}

}
