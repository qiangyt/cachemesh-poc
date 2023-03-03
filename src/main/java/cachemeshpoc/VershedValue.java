package cachemeshpoc;

@lombok.Getter
@lombok.ToString
public class VershedValue {

	private final Object data;

	private final long versh;

	public VershedValue(Object data, long versh) {
		this.data = data;
		this.versh = versh;
	}

}
