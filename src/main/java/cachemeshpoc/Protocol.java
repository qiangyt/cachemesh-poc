package cachemeshpoc;

public enum Protocol {

	//resp3,

	grpc;

	public static Protocol Default() {
		return grpc;
	}

}
