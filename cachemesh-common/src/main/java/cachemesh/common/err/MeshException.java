package cachemesh.common.err;

public class MeshException extends RuntimeException {

	public MeshException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public MeshException(Throwable cause) {
		super(cause);
	}

	public MeshException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
