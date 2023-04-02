package cachemesh.core.config;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class NodesUdpBroadcast {

	private int port;

	public NodesUdpBroadcast(int port) {
		this.port = port;
	}

}
