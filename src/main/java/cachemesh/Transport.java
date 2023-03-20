package cachemesh;

public enum Transport {

	//resp3,

	redis,

	grpc;

	public static Transport Default() {
		return grpc;
	}

}
