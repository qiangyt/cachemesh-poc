package cachemesh.config;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class MeshConfig {

	private Nodes nodes;

	private Serders serders;

	private Map<String, LocalCache> localCaches;

	public MeshConfig(Nodes nodes, Serders serders, Map<String, LocalCache> localCaches) {
		this.nodes = nodes;
		this.serders = serders;
		this.localCaches = localCaches;
	}

	public static void main(String[] args) throws Exception {
		//var customTypeDescription = new TypeDescription(MeshConfig.class);
		//customTypeDescription.addPropertyParameters("contactDetails", Contact.class);
		//constructor.addTypeDescription(customTypeDescription);

		Yaml yaml = new Yaml();

		InputStream inputStream = MeshConfig.class.getClassLoader().getResourceAsStream("example.yaml");
		MeshConfig config = yaml.loadAs(inputStream, MeshConfig.class);
		System.out.println(config);
	}

}
