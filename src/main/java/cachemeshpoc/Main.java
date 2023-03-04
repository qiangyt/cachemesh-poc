package cachemeshpoc;

import java.net.URL;

import cachemeshpoc.grpc.GrpcCacheManager;
import cachemeshpoc.grpc.GrpcClientFactory;
import cachemeshpoc.grpc.GrpcConfig;
import cachemeshpoc.local.caffeine.CaffeineLocalCacheFactory;

public class Main {

	public static void main(String[] args) throws Exception {
		var caffeineFactory = new CaffeineLocalCacheFactory();
		var meshCacheManager = new MeshNet(caffeineFactory);

		var grpcClientFactory = new GrpcClientFactory();

		var url1 = new URL("grpc://localhost:60001");
		var grpcConfig1 = GrpcConfig.from(url1);
		var grpcClient1 = grpcClientFactory.create(grpcConfig1);
		var cacheManager1 = new GrpcCacheManager(grpcClient1);
		var node1 = new MeshNode(url1, cacheManager1);
		meshCacheManager.addNode(node1);

		var url2 = new URL("grpc://localhost:60002");
		var grpcConfig2 = GrpcConfig.from(url2);
		var cacheManager2 = new SideCacheManager(caffeineFactory);
		var node2 = new MeshNode(url2, cacheManager2);
		meshCacheManager.addNode(node2);

		var meshCache = meshCacheManager.resolveCache("example", String.class);
		meshCache.putSingle("k1", "v1");

		String v1 = meshCache.getSingle("k1");
		System.out.println("getSingle(key) returns:" + v1);

		meshCacheManager.close();
	}


}
