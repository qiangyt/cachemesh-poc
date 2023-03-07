package cachemeshpoc.config;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class NodeRedis {

	private int database;

	public NodeRedis(int database) {
		this.database = database;
	}

}
