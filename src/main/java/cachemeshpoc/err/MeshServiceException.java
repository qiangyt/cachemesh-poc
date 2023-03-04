package cachemeshpoc.err;

public class MeshServiceException extends MeshInternalException {

	public MeshServiceException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public MeshServiceException(Throwable cause) {
		super(cause);
	}

	public MeshServiceException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
