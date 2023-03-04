package cachemeshpoc.err;

public class MeshRequestException extends MeshException {

	public MeshRequestException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public MeshRequestException(Throwable cause) {
		super(cause);
	}

	public MeshRequestException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
