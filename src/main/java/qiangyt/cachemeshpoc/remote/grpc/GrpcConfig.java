package qiangyt.cachemeshpoc.remote.grpc;

@lombok.Getter
////@lombok.NoArgsConstructor
//@lombok.Builder(buildMethodName = "buildConfig")
////@lombok.experimental.SuperBuilder
public class GrpcConfig {

	private final int port;

	private final String host;

	public GrpcConfig(int port, String host) {
		this.port = port;
		this.host = host;
	}

	public String getTarget() {
		return String.format("%s:%d", this.host, this.port);
	}

}
