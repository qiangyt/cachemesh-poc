package cachemeshpoc.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

public class GrpcClientFactory {

	public GrpcClient create(GrpcConfig config) {
		var ch = Grpc.newChannelBuilder(config.getTarget(), InsecureChannelCredentials.create()).build();
		return new GrpcClient(config, ch);
	}

}
