package cachemeshpoc.remote.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;

public class GrpcClientFactory extends GrpcConfigBuilder {

	public GrpcClient create() {
		var cfg = buildConfig();
		var ch = Grpc.newChannelBuilder(cfg.getTarget(), InsecureChannelCredentials.create()).build();
		return new GrpcClient(cfg, ch);
	}

}
