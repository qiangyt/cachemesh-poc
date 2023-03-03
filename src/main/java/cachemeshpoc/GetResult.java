package cachemeshpoc;

@lombok.ToString
@lombok.Getter
public class GetResult {

	public static enum Status {
		NOT_FOUND,
		OK,
		NO_CHANGE,
		REDIRECT,
	}


	public static final GetResult NOT_FOUND
		= new GetResult(Status.NOT_FOUND, null, 0);

	public static final GetResult NO_CHANGE
		= new GetResult(Status.NO_CHANGE, null, 0);

	private final Status status;

	private final  byte[] bytes;

	private final long versh;

	public GetResult(Status status, byte[] bytes, long versh) {
		this.status = status;
		this.bytes = bytes;
		this.versh = versh;
	}

}
