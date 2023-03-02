package cachemeshpoc.local;


import cachemeshpoc.local.Entry.Value;
import cachemeshpoc.local.Entry.Status;

@lombok.Getter
@lombok.ToString
public class Result<T> {

	public static final Result<?> NOT_FOUND = new Result<>(Status.NOT_FOUND, null);

	public static final Result<?> NO_CHANGE = new Result<>(Status.NO_CHANGE, null);

	private final Status status;

	private final Value<T> value;

	public Result(Status status, Value<T> value) {
		this.status = status;
		this.value = value;
	}

}
