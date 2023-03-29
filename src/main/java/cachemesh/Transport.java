package cachemesh;

public enum Transport {

	//resp3,

	redis,

	grpc;

	public static Transport Default() {
		return grpc;
	}

	public String url(String target) {
		return String.format("%s://%s", name(), target);
	}

}
