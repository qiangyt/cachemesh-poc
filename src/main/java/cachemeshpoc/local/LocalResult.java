package cachemeshpoc.local;


import cachemeshpoc.local.Entry.Value;
import cachemeshpoc.local.Entry.Status;

@lombok.Getter
@lombok.ToString
public class LocalResult<T> {

	public static final LocalResult<?> NOT_FOUND = new LocalResult<>(Status.NOT_FOUND, null);

	public static final LocalResult<?> NO_CHANGE = new LocalResult<>(Status.NO_CHANGE, null);

	private final Status status;

	private final Value<T> value;

	public LocalResult(Status status, Value<T> value) {
		this.status = status;
		this.value = value;
	}

}
