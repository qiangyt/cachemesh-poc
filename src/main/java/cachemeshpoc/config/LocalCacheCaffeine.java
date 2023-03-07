package cachemeshpoc.config;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
public class LocalCacheCaffeine {

	private int maximumSize;

	private int expireAfterWrite;

	public LocalCacheCaffeine(int maximumSize, int expireAfterWrite) {
		this.maximumSize = maximumSize;
		this.expireAfterWrite = expireAfterWrite;
	}
}
