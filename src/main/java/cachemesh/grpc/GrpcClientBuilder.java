package cachemesh.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

public class GrpcClientBuilder {

	public GrpcClient build(GrpcConfig config) {
		var ch = Grpc.newChannelBuilder(config.getTarget(), InsecureChannelCredentials.create()).build();
		return new GrpcClient(config, ch);
	}

}
