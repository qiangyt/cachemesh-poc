package cachemeshpoc.remote.grpc;

import io.grpc.stub.StreamObserver;
import cachemeshpoc.MeshRouter;
import cachemeshpoc.Serderializer;
import cachemeshpoc.local.LocalCache;

import com.google.protobuf.ByteString;

public class GrpcService extends CacheMeshGrpc.CacheMeshImplBase {

	private final LocalCache.Manager sideCaches;

	private final MeshRouter router;

	private final Serderializer serder;

	public GrpcService(LocalCache.Manager sideCaches, MeshRouter router, Serderializer serder) {
		this.sideCaches = sideCaches;
		this.router = router;
		this.serder = serder;
	}

	@Override
	public void resolveSingle(ResolveSingleRequest req, StreamObserver<ResolveSingleResponse> respObserver) {
		String key = req.getKey();

		var builder = ResolveSingleResponse.newBuilder();

		var node = this.router.findNode(key);
		if (!this.router.isSelfNode(node)) {
			// TODO: redirect to which
			builder.setStatus(ValueStatus.Redirect);
		} else {
			var localCache = this.sideCaches.get(req.getCacheName());
			if (localCache == null) {
				builder.setStatus(ValueStatus.NotFound);
			} else {
				var value = localCache.getSingle(key);
				if (value == null) {
					builder.setStatus(ValueStatus.NotFound);
				} else {
					long localVersion = value.getVersion();
					if (req.getVersion() <= localVersion) {
						builder.setStatus(ValueStatus.NoChange);
					} else {
						builder.setStatus(ValueStatus.Changed);

						byte[] valueBytes = value.getBytes(this.serder);
						builder.setVersion(localVersion).setValue(ByteString.copyFrom(valueBytes));
					}
				}
			}
		}

		GrpcResponses.complete(respObserver, builder.build());
	}

	@Override
	public void putSingle(PutSingleRequest req, StreamObserver<PutSingleResponse> respObserver) {
		String key = req.getKey();

		var builder = PutSingleResponse.newBuilder();

		var node = this.router.findNode(key);
		if (!this.router.isSelfNode(node)) {
			// TODO: redirect to which
			builder.setStatus(ValueStatus.Redirect);
		} else {
			builder.setStatus(ValueStatus.Changed);

			var localCache = this.sideCaches.resolve(req.getCacheName());

			long version;
			var oldValue = localCache.getSingle(key);
			if (oldValue != null) {
				version = oldValue.getVersion() + 1;
			} else {
				version = 1;
			}
			builder.setVersion(version);

			var newValue = EntryValue.builder().bytes(req.getValue().toByteArray()).version(version).build();
			localCache.putSingle(key, newValue);
		}

		GrpcResponses.complete(respObserver, builder.build());
	}

}
