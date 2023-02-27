package com.github.qiangyt.cachemeshpoc.grpc;

@lombok.Getter
//@lombok.NoArgsConstructor
@lombok.Builder(buildMethodName = "buildConfig")
//@lombok.experimental.SuperBuilder
public class GrpcRemoteCacheConfig {

	private final int port;

	private final String host;

	public GrpcRemoteCacheConfig(int port, String host) {
		this.port = port;
		this.host = host;
	}

	public String target() {
		return String.format("%s:%d", this.host, this.port);
	}

}
