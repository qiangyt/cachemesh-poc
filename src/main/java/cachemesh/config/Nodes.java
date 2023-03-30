package cachemesh.config;

import java.util.HashMap;
import java.util.Map;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class Nodes {

	private String provider;

	private Map<String, Map<String,Object>> providers = new HashMap<>();

	private Node[] list;

	public Nodes(String provider, NodesUdpBroadcast udpBroadcast, Node[] list) {
		this.provider = provider;
		this.udpBroadcast = udpBroadcast;
		this.list = list;
	}

}
