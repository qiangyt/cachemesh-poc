package cachemeshpoc.remote;

@lombok.Builder
@lombok.Data
public class ResolveSingleResult {

	public static final ResolveSingleResult NOT_FOUND
		= builder()
			.status(RemoteValueStatus.NotFound)
			.value(null)
			.version(0)
			.build();

	public static final ResolveSingleResult NO_CHANGE
		= builder()
			.status(RemoteValueStatus.NoChange)
			.value(null)
			.version(0)
			.build();


	private final RemoteValueStatus status;

	private final  byte[] value;

	private final long version;

}
