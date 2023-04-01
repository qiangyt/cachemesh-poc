package cachemesh.core;

import lombok.Getter;

@Getter
public class GetResult<T> {

	public static final GetResult<?> NOT_FOUND
		= new GetResult<>(ResultStatus.NOT_FOUND, null, 0);

	public static final GetResult<?> NO_CHANGE
		= new GetResult<>(ResultStatus.NO_CHANGE, null, 0);

	private final ResultStatus status;

	private final T value;

	private final long version;

	public GetResult(ResultStatus status, T value, long version) {
		this.status = status;
		this.value = value;
		this.version = version;
	}

}
