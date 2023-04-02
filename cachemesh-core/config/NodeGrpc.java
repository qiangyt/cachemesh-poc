package cachemesh.core.config;

import java.time.Duration;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class NodeGrpc {

	private Duration serviceShutdown;

	private Duration clientShutdown;

	public NodeGrpc(Duration serviceShutdown, Duration clientShutdown) {
		this.serviceShutdown = serviceShutdown;
		this.clientShutdown = clientShutdown;
	}

}
