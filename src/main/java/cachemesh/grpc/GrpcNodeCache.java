package cachemesh.grpc;

import cachemesh.core.GetResult;
import cachemesh.spi.NodeCache;

import com.google.protobuf.ByteString;


public class GrpcNodeCache implements NodeCache {

	private final CacheMeshGrpc.CacheMeshBlockingStub stub;
	private final GrpcClient client;

	public GrpcNodeCache(GrpcClient client) {
		this.stub = CacheMeshGrpc.newBlockingStub(client.getChannel());
		this.client = client;
	}

	@Override
	public void shutdown(int timeoutSeconds) throws InterruptedException {
		this.client.shutdown(timeoutSeconds);
	}

	@Override
	public GetResult<byte[]> getSingle(String cacheName, String key, long version) {
		var req = GetSingleRequest.newBuilder()
					.setCacheName(cacheName)
					.setKey(key)
					.setVersion(version)
					.build();

		var resp = this.stub.getSingle(req);
		/*try {
			resp = stub.getSingle(req);
		} catch (StatusRuntimeException e) {
			LOG.warn("Get single key RPC failed: {}", e.getStatus());
			return null;
		} */

		//TODO: how to indicate we do have the value but the value is null
		var respV = resp.getValue();
		var v = (respV == null) ? null : respV.toByteArray();
		return new GetResult<>(GrpcHelper.convertStatus(resp.getStatus()), v, resp.getVersion());
	}

	@Override
	public long putSingle(String cacheName, String key, byte[] value) {
		var req = PutSingleRequest.newBuilder()
					.setCacheName(cacheName)
					.setKey(key);
		if (value != null) {
			req.setValue(ByteString.copyFrom(value));
		}

		var resp = this.stub.putSingle(req.build());
		return resp.getVersion();
	}

}
