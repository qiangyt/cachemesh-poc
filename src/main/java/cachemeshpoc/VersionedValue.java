package cachemeshpoc;

@lombok.Getter
@lombok.ToString
public class VersionedValue {

	private final Object data;

	private final long version;

	public VersionedValue(Object data, long version) {
		this.data = data;
		this.version = version;
	}

}
