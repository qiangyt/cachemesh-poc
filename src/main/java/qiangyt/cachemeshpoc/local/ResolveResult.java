package qiangyt.cachemeshpoc.local;

@lombok.Data
@lombok.Builder
public class ResolveResult {

	public static final ResolveResult NOT_FOUND
		= builder()
			.value(null)
			.status(ValueStatus.NotFound)
			.build();

	public static final ResolveResult NO_CHANGE
		= builder()
			.value(null)
			.status(ValueStatus.NoChange)
			.build();

	private final ValueStatus status;

	private final EntryValue value;

}
