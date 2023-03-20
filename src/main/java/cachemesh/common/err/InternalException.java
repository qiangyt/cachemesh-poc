package cachemesh.common.err;

public class InternalException extends MeshException {

	public InternalException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public InternalException(Throwable cause) {
		super(cause);
	}

	public InternalException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
