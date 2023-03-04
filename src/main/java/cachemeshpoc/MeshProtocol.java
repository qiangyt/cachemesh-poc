package cachemeshpoc;

import cachemeshpoc.url.Handler;

public enum MeshProtocol {

	//resp3,

	grpc;


	static {
		Handler.registerHandler();
	}

	public static MeshProtocol Default() {
		return grpc;
	}

}
