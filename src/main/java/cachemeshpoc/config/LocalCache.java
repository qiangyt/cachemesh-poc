package cachemeshpoc.config;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class LocalCache {

	private String provider;

	private LocalCacheCaffeine caffeine;

	public LocalCache(String provider, LocalCacheCaffeine caffeine) {
		this.provider = provider;
		this.caffeine = caffeine;
	}

}
