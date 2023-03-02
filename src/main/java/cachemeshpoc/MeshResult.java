package cachemeshpoc;

@lombok.ToString
@lombok.Getter
public class MeshResult {

	public static enum Status {
		NOT_FOUND,
		OK,
		NO_CHANGE,
		REDIRECTED,
	}


	public static final MeshResult NOT_FOUND
		= new MeshResult(Status.NOT_FOUND, null, 0);

	public static final MeshResult NO_CHANGE
		= new MeshResult(Status.NO_CHANGE, null, 0);

	private final Status status;

	private final  byte[] bytes;

	private final long version;

	public MeshResult(Status status, byte[] bytes, long version) {
		this.status = status;
		this.bytes = bytes;
		this.version = version;
	}

}
