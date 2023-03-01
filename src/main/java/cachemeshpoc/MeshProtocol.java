package cachemeshpoc;

public enum MeshProtocol {

	//resp3,

	grpc;

	static MeshProtocol Default() {
		return grpc;
	}

}
