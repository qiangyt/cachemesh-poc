package cachemeshpoc;

public enum Protocol {

	//resp3,

	redis,

	grpc;

	public static Protocol Default() {
		return grpc;
	}

}
