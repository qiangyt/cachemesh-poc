package cachemesh.spi.base;

@lombok.Getter
@lombok.ToString
public class Value<T> {

	private final T data;

	private final long version;

	public Value(T data, long version) {
		this.data = data;
		this.version = version;
	}

}
