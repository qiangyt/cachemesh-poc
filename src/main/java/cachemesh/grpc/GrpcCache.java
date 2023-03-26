package cachemesh.grpc;

import cachemesh.spi.NodeCache;
import cachemesh.spi.base.GetResult;

import com.google.protobuf.ByteString;


public class GrpcCache implements NodeCache {

	private final GrpcClient client;

	private final CacheMeshGrpc.CacheMeshBlockingStub stub;


	public GrpcCache(GrpcClient client) {
		this.client = client;
		this.stub = CacheMeshGrpc.newBlockingStub(client.getChannel());
	}

	@Override
	public String getName() {
		return this.client.getName();
	}

	@Override
	public GetResult<byte[]> getSingle(String key, long version) {
		var req = GetSingleRequest.newBuilder()
					.setCacheName(getName())
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

		var v = (resp.getValue() == null) ? null : resp.getValue().toByteArray();
		return new GetResult<>(GrpcHelper.convertStatus(resp.getStatus()), v, resp.getVersion());
	}

	@Override
	public long putSingle(String key, byte[] value) {
		var req = PutSingleRequest.newBuilder()
					.setCacheName(getName())
					.setKey(key)
					.setValue(ByteString.copyFrom(value))
					.build();
		var resp = this.stub.putSingle(req);
		return resp.getVersion();
	}

}
