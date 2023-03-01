package cachemeshpoc.err;

public class CacheMeshException extends RuntimeException {

	protected CacheMeshException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	protected CacheMeshException(Throwable cause) {
		super(cause);
	}

	protected CacheMeshException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
