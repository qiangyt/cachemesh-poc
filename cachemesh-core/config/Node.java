package cachemesh.core.config;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class Node {

	private String url;

	private boolean local;

	private NodeGrpc grpc;

	private NodeRedis redis;

	public Node(String url, boolean local, NodeGrpc grpc, NodeRedis redis) {
		this.url = url;
		this.local = local;
		this.grpc = grpc;
		this.redis = redis;
	}

}
