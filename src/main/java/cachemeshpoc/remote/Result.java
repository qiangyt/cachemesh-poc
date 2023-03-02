package cachemeshpoc.remote;

@lombok.ToString
@lombok.Getter
public class Result {

	public static enum Status {
		NOT_FOUND,
		CHANGED,
		NO_CHANGE,
		REDIRECTED,
	}


	public static final Result NOT_FOUND
		= new Result(Status.NOT_FOUND, null, 0);

	public static final Result NO_CHANGE
		= new Result(Status.NO_CHANGE, null, 0);

	private final Status status;

	private final  byte[] value;

	private final long version;

	public Result(Status status, byte[] value, long version) {
		this.status = status;
		this.value = value;
		this.version = version;
	}

}
