package cachemeshpoc.err;

public class MeshInternalException extends MeshException {

	public MeshInternalException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public MeshInternalException(Throwable cause) {
		super(cause);
	}

	public MeshInternalException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
