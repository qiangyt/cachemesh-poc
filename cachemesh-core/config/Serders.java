package cachemesh.core.config;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class Serders {

	private String provider;

	private SerderJackson jackson;

	public Serders(String provider, SerderJackson jackson) {
		this.provider = provider;
		this.jackson = jackson;
	}

}
