package cachemeshpoc;

import cachemeshpoc.url.Handler;

public enum Protocol {

	//resp3,

	grpc;


	static {
		Handler.registerHandler();
	}

	public static Protocol Default() {
		return grpc;
	}

}
