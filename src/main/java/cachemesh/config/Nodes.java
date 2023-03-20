package cachemesh.config;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class Nodes {

	private String provider;

	private NodesUdpBroadcast udpBroadcast;

	private Node[] list;

	public Nodes(String provider, NodesUdpBroadcast udpBroadcast, Node[] list) {
		this.provider = provider;
		this.udpBroadcast = udpBroadcast;
		this.list = list;
	}

}
