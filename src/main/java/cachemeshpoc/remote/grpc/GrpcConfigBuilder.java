package cachemeshpoc.remote.grpc;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class GrpcConfigBuilder {

	private int port;

	private String host;

	public GrpcConfig buildConfig() {
		return new GrpcConfig(this.port, this.host);
	}

}
