package cachemeshpoc.err;

public class MeshException extends RuntimeException {

	protected MeshException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	protected MeshException(Throwable cause) {
		super(cause);
	}

	protected MeshException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
