package cachemeshpoc;

@lombok.ToString
@lombok.Getter
public class GetResult {

	public static final GetResult NOT_FOUND
		= new GetResult(ResultStatus.NOT_FOUND, null, 0);

	public static final GetResult NO_CHANGE
		= new GetResult(ResultStatus.NO_CHANGE, null, 0);

	private final ResultStatus status;

	private final  byte[] bytes;

	private final long versh;

	public GetResult(ResultStatus status, byte[] bytes, long versh) {
		this.status = status;
		this.bytes = bytes;
		this.versh = versh;
	}

}
