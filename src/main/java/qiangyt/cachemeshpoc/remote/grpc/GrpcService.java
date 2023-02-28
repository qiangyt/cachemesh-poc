package qiangyt.cachemeshpoc.remote.grpc;

import io.grpc.stub.StreamObserver;

import qiangyt.cachemeshpoc.local.LocalCacheManager;
import qiangyt.cachemeshpoc.local.LocalEntry;
import qiangyt.cachemeshpoc.route.MeshRouter;
import qiangyt.cachemeshpoc.serde.Serderializer;
import com.google.protobuf.ByteString;

public class GrpcService extends CacheMeshGrpc.CacheMeshImplBase {

	private final LocalCacheManager localCacheManager;

	private final MeshRouter router;

	private final Serderializer serder;

	public GrpcService(LocalCacheManager localCacheManager, MeshRouter router, Serderializer serder) {
		this.localCacheManager = localCacheManager;
		this.router = router;
		this.serder = serder;
	}

	@Override
	public void getSingle(GetSingleRequest req, StreamObserver<GetSingleResponse> respObserver) {
		String key = req.getKey();
		var node = this.router.findNode(key);
		if (!this.router.isSelfNode(node)) {
			// TODO: redirect to which
			var resp = GetSingleResponse.newBuilder().setStatus(ValueStatus.Redirect).build();
			respObserver.onNext(resp);
			respObserver.onCompleted();
			return;
		}

		String cacheName = req.getCache();
		var localCache = this.localCacheManager.get(cacheName, null);
		if (localCache == null) {
			var resp = GetSingleResponse.newBuilder().setStatus(ValueStatus.NotFound).build();
			respObserver.onNext(resp);
			respObserver.onCompleted();
			return;
		}

		var localEntry = localCache.getSingle(key);
		if (localEntry == null) {
			var resp = GetSingleResponse.newBuilder().setStatus(ValueStatus.NotFound).build();
			respObserver.onNext(resp);
			respObserver.onCompleted();
			return;
		}

		long version = req.getVersion();
		long localVersion = localEntry.getVersion();
		if (version <= localVersion) {
			var resp = GetSingleResponse.newBuilder().setStatus(ValueStatus.NoChange).build();
			respObserver.onNext(resp);
			respObserver.onCompleted();
			return;
		}

		byte[] valueBytes = this.serder.serialize(localEntry.getValue());

		var resp = GetSingleResponse.newBuilder()
				.setStatus(ValueStatus.Changed)
				.setVersion(localVersion)
				.setValue(ByteString.copyFrom(valueBytes))
				.build();
		respObserver.onNext(resp);
		respObserver.onCompleted();
	}

	@Override
	public void setSingle(SetSingleRequest req, StreamObserver<SetSingleResponse> respObserver) {
		String key = req.getKey();
		var node = this.router.findNode(key);
		if (!this.router.isSelfNode(node)) {
			// TODO: redirect to which
			var resp = SetSingleResponse.newBuilder().setStatus(ValueStatus.Redirect).build();
			respObserver.onNext(resp);
			respObserver.onCompleted();
			return;
		}

		String cacheName = req.getCache();
		var localCache = this.localCacheManager.resolve(cacheName, null);// TODO

		var newLocalEntry = new LocalEntry<Object>();// TODO
		newLocalEntry.setKey(req.getKey());

		byte[] valueBytes = req.getValue().toByteArray();
		var valueObj = this.serder.deserialize(valueBytes, localCache.getValueClass());
		newLocalEntry.setValue(valueObj);

		long version;
		var exisitingLocalEntry = localCache.getSingle(key);
		if (exisitingLocalEntry != null) {
			version = exisitingLocalEntry.getVersion();
		} else {
			version = 1;
		}
		newLocalEntry.setVersion(version);

		localCache.setSingle(newLocalEntry);

		var resp = SetSingleResponse.newBuilder()
				.setStatus(ValueStatus.Changed)
				.setVersion(version)
				.build();
		respObserver.onNext(resp);
		respObserver.onCompleted();
	}

}
