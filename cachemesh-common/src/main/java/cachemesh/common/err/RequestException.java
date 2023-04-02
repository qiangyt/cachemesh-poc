package cachemesh.common.err;

public class RequestException extends MeshException {

	public RequestException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public RequestException(Throwable cause) {
		super(cause);
	}

	public RequestException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
